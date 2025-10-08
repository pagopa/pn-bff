const { handleEvent } = require('../app/eventHandler');
const s3Utils = require('../app/s3Utils');
const api = require('../app/raddClient');
const csvUtils = require('../app/csvUtils');
const ssmUtils = require('../app/ssmParameter');
const storeLocatorCsvEntity = require('../app/StoreLocatorCsvEntity');
const utils = require('../app/utils');
const sinon = require('sinon');
const assert = require('node:assert/strict');
const { raddAltApiResponse } = require('../__mocks__/registries.mock');
const { csvConfigurationMock } = require('../__mocks__/csvConfiguration.mock');
const { setupEnv } = require('./utils/test.utils');

describe('handler generates new file', () => {
  let wrongAddressRecordCallCount = 0;

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

    wrongAddressRecordCallCount = raddAltApiResponse.reduce((count, record) => {
      const { isRecordValid } =
        storeLocatorCsvEntity.mapApiResponseToStoreLocatorCsvEntities(record);
      return count + (isRecordValid ? 0 : 1);
    }, 0);
  });

  afterEach(() => {
    sinon.restore();
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
      raddAltApiResponse.length + wrongAddressRecordCallCount * 2
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
      raddAltApiResponse.length + wrongAddressRecordCallCount * 2
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
      raddAltApiResponse.length + wrongAddressRecordCallCount * 2
    );
    sinon.assert.calledOnce(utils.checkIfIntervalPassed);
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
          record: {
            locationId: '"12345"',
            description: '"Test Store"',
            cafAddress: '"VIA NAZIONE 1, 20100 ROMA"',
            normalizedAddress: '"Via Nazionale 15, 20100 RM, ROMA"',
            biasPoint:
              '{"addressNumber":0.95,"country":1,"locality":1,"postalCode":0.9,"subRegion":1,"overall":0.98}',
          },
          isRecordValid: false,
          isCAPValid: false,
        };
      });

    const csvHeader =
      'locationId;descrizione;indirizzo_originale;indirizzo_normalizzato;score_AWS\n';
    const csvLine =
      '"12345";"Test Store";"VIA NAZIONE 1, 20100 ROMA";"Via Nazionale 15, 20100 RM, ROMA";{"addressNumber":0.95,"country":1,"locality":1,"postalCode":0.9,"subRegion":1,"overall":0.98}\n';

    await handleEvent({});

    sinon.assert.callCount(s3Utils.uploadVersionedFile, 3);

    const malformedAddressCall = s3Utils.uploadVersionedFile.getCall(0);

    // sendToWebLanding
    assert.strictEqual(malformedAddressCall.args[0], false);

    // bffBucketS3Key
    assert.strictEqual(
      malformedAddressCall.args[1],
      `${process.env.BFF_BUCKET_PREFIX}/malformed_addresses.csv`
    );

    // csvContent
    assert.match(malformedAddressCall.args[2], new RegExp(csvHeader + csvLine));

    // --- Also have to add this to wrong postal codes CSV
    const wrongPostalCodesCall = s3Utils.uploadVersionedFile.getCall(1);

    // sendToWebLanding
    assert.strictEqual(wrongPostalCodesCall.args[0], false);

    // bffBucketS3Key
    assert.strictEqual(
      wrongPostalCodesCall.args[1],
      `${process.env.BFF_BUCKET_PREFIX}/wrong_postal_codes.csv`
    );

    // csvContent
    assert.match(malformedAddressCall.args[2], new RegExp(csvHeader + csvLine));
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
