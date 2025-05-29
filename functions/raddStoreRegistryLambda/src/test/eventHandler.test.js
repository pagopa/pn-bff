const { handleEvent } = require('../app/eventHandler');
const s3Utils = require('../app/s3Utils');
const api = require('../app/raddClient');
const csvUtils = require('../app/csvUtils');
const ssmUtils = require('../app/ssmParameter');
const utils = require('../app/utils');
const sinon = require('sinon');
const assert = require('node:assert/strict');
const storeLocatorCsvEntity = require('../app/StoreLocatorCsvEntity');
const {
  raddAltApiResponse,
  mockGeocodeResponse,
} = require('../__mocks__/registries.mock');
const { csvConfigurationMock } = require('../__mocks__/csvConfiguration.mock');
const { mockClient } = require('aws-sdk-client-mock');
const {
  GeoPlacesClient,
  GeocodeCommand,
} = require('@aws-sdk/client-geo-places');
const { setupEnv } = require('./utils/test.utils');

describe('handler generates new file', () => {
  let placesClientMock;

  beforeEach(() => {
    setupEnv();

    sinon
      .stub(ssmUtils, 'retrieveCsvConfiguration')
      .resolves(csvConfigurationMock);

    sinon.stub(api, 'fetchApi').resolves({
      registries: raddAltApiResponse,
      lastKey: null,
    });

    sinon.stub(csvUtils, 'validateCsvConfiguration').returns();
    sinon.stub(s3Utils, 'uploadVersionedFile').returns();

    sinon.spy(csvUtils, 'createCSVContent');

    placesClientMock = mockClient(GeoPlacesClient);

    placesClientMock.on(GeocodeCommand).resolves(mockGeocodeResponse);
  });

  afterEach(() => {
    sinon.restore();
    placesClientMock.reset();
  });

  it('generates new file when forceGenerate is true', async () => {
    sinon.stub(s3Utils, 'getLatestVersion').resolves(null);

    sinon.stub(ssmUtils, 'retrieveGenerationConfigParameter').resolves({
      forceGenerate: true,
      sendToWebLanding: true,
    });

    sinon.stub(utils, 'checkIfIntervalPassed').returns(false);

    await handleEvent({});

    sinon.assert.calledOnce(ssmUtils.retrieveCsvConfiguration);
    sinon.assert.calledOnce(ssmUtils.retrieveGenerationConfigParameter);
    sinon.assert.calledOnce(s3Utils.getLatestVersion);
    sinon.assert.calledOnce(api.fetchApi);
    sinon.assert.calledOnce(csvUtils.validateCsvConfiguration);
    sinon.assert.callCount(
      csvUtils.createCSVContent,
      raddAltApiResponse.length
    );
    sinon.assert.notCalled(utils.checkIfIntervalPassed);
  });

  it('generates new file when doesnt find generationConfiguration and there is no previous file on bucket', async () => {
    sinon.stub(s3Utils, 'getLatestVersion').resolves(null);

    sinon.stub(ssmUtils, 'retrieveGenerationConfigParameter').resolves(null);

    sinon.stub(utils, 'checkIfIntervalPassed').returns(false);

    await handleEvent({});

    sinon.assert.calledOnce(ssmUtils.retrieveCsvConfiguration);
    sinon.assert.calledOnce(ssmUtils.retrieveGenerationConfigParameter);
    sinon.assert.calledOnce(s3Utils.getLatestVersion);
    sinon.assert.calledOnce(api.fetchApi);
    sinon.assert.calledOnce(csvUtils.validateCsvConfiguration);
    sinon.assert.callCount(
      csvUtils.createCSVContent,
      raddAltApiResponse.length
    );
    sinon.assert.notCalled(utils.checkIfIntervalPassed);
  });

  it('generates new file when doesnt find generationConfiguration and interval passed', async () => {
    sinon.stub(s3Utils, 'getLatestVersion').resolves({
      Key: 'your-key',
      VersionId: 'your-version-id',
      IsLatest: true,
      LastModified: new Date(),
      Owner: {
        DisplayName: 'your-display-name',
        ID: 'your-id',
      },
      Size: 123,
      StorageClass: 'STANDARD',
    });

    sinon.stub(ssmUtils, 'retrieveGenerationConfigParameter').resolves(null);

    sinon.stub(utils, 'checkIfIntervalPassed').returns(true);

    await handleEvent({});

    sinon.assert.calledOnce(ssmUtils.retrieveCsvConfiguration);
    sinon.assert.calledOnce(ssmUtils.retrieveGenerationConfigParameter);
    sinon.assert.calledOnce(s3Utils.getLatestVersion);
    sinon.assert.calledOnce(api.fetchApi);
    sinon.assert.calledOnce(csvUtils.validateCsvConfiguration);
    sinon.assert.callCount(
      csvUtils.createCSVContent,
      raddAltApiResponse.length
    );
    sinon.assert.calledOnce(utils.checkIfIntervalPassed);
  });

  it('properly handles batching and sleep timing between batches', async () => {
    sinon.stub(s3Utils, 'getLatestVersion').resolves(null);
    sinon.stub(ssmUtils, 'retrieveGenerationConfigParameter').resolves({
      forceGenerate: true,
      sendToWebLanding: true,
    });

    const firstBatch = {
      registries: raddAltApiResponse,
      lastKey: 'next',
    };
    const secondBatch = {
      registries: raddAltApiResponse,
      lastKey: null,
    };

    if (api.fetchApi.restore) {
      api.fetchApi.restore();
    }

    const fetchApiStub = sinon.stub(api, 'fetchApi');
    fetchApiStub.onCall(0).resolves(firstBatch);
    fetchApiStub.onCall(1).resolves(secondBatch);

    const clock = sinon.useFakeTimers();

    const handlePromise = handleEvent({});
    await Promise.resolve();
    await clock.tickAsync(1000);
    await handlePromise;

    sinon.assert.calledTwice(api.fetchApi);
    sinon.assert.callCount(
      csvUtils.createCSVContent,
      firstBatch.registries.length + secondBatch.registries.length
    );

    clock.restore();
  });

  it('generates new file and uploads malformed addresses CSV when found', async () => {
    sinon.stub(s3Utils, 'getLatestVersion').resolves(null);

    sinon.stub(ssmUtils, 'retrieveGenerationConfigParameter').resolves({
      forceGenerate: true,
      sendToWebLanding: true,
    });

    sinon.stub(utils, 'checkIfIntervalPassed').returns(false);

    sinon
      .stub(storeLocatorCsvEntity, 'mapApiResponseToStoreLocatorCsvEntities')
      .callsFake(() => {
        return {
          storeRecord: null,
          malformedRecord: {
            description: 'Test Store',
            city: 'Roma',
            address: 'Via Nazionale 15',
            province: 'RM',
            awsAddress: 'Via Nazionale 15, 20100 RM, ROMA',
            awsScore: 0.9,
            awsLatitude: 42.6741,
            awsLongiotude: 11.9082,
          },
        };
      });

    await handleEvent({});

    sinon.assert.callCount(s3Utils.uploadVersionedFile, 2);

    const malformedAddressCall = s3Utils.uploadVersionedFile.secondCall;

    // sendToWebLanding
    assert.strictEqual(malformedAddressCall.args[0], false);

    // bffBucketS3Key
    assert.strictEqual(
      malformedAddressCall.args[1],
      `${process.env.BFF_BUCKET_PREFIX}/malformed_addresses.csv`
    );

    // csvContent
    assert.match(
      malformedAddressCall.args[2],
      /descrizione;indirizzo;citta;provincia;indirizzo AWS;score AWS;latitudine;longitudine/
    );

    assert.match(
      malformedAddressCall.args[2],
      /Test Store;Via Nazionale 15;Roma;RM;Via Nazionale 15, 20100 RM, ROMA;0.9;42.6741;/
    );
  });
});

describe('handler doesnt generate new file', () => {
  beforeEach(() => {
    setupEnv();

    sinon.stub(ssmUtils, 'retrieveCsvConfiguration').resolves({
      configurationVersion: '1.0',
      configs: [],
    });

    sinon.stub(api, 'fetchApi');

    sinon.stub(csvUtils, 'validateCsvConfiguration');
    sinon.stub(csvUtils, 'createCSVContent');
  });

  afterEach(() => {
    sinon.restore();
  });

  it('doesnt generate new file when interval is not passed', async () => {
    sinon.stub(s3Utils, 'getLatestVersion').resolves({
      Key: 'your-key',
      VersionId: 'your-version-id',
      IsLatest: true,
      LastModified: new Date(),
      Owner: {
        DisplayName: 'your-display-name',
        ID: 'your-id',
      },
      Size: 123,
      StorageClass: 'STANDARD',
    });

    sinon.stub(ssmUtils, 'retrieveGenerationConfigParameter').resolves({
      forceGenerate: false,
      sendToWebLanding: true,
    });

    sinon.stub(utils, 'checkIfIntervalPassed').returns(false);

    await handleEvent({});

    sinon.assert.calledOnce(ssmUtils.retrieveCsvConfiguration);
    sinon.assert.calledOnce(ssmUtils.retrieveGenerationConfigParameter);
    sinon.assert.calledOnce(s3Utils.getLatestVersion);
    sinon.assert.calledOnce(utils.checkIfIntervalPassed);
    sinon.assert.notCalled(api.fetchApi);
    sinon.assert.notCalled(csvUtils.validateCsvConfiguration);
    sinon.assert.notCalled(csvUtils.createCSVContent);
  });
});

describe('handler throws error for missing required env', () => {
  it('throws error when BFF_BUCKET_NAME is missing', async () => {
    process.env = {};
    await assert.rejects(handleEvent({}), {
      message: 'Missing required environment variable: BFF_BUCKET_NAME',
    });
  });
});
