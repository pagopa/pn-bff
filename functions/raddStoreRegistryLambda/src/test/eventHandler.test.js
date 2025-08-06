const { handleEvent } = require('../app/eventHandler');
const s3Utils = require('../app/s3Utils');
const api = require('../app/raddClient');
const csvUtils = require('../app/csvUtils');
const ssmUtils = require('../app/ssmParameter');
const utils = require('../app/utils');
const sinon = require('sinon');
const assert = require('node:assert/strict');
const { raddAltApiResponse } = require('../__mocks__/registries.mock');
const { csvConfigurationMock } = require('../__mocks__/csvConfiguration.mock');
const { setupEnv } = require('./utils/test.utils');

describe('handler generates new file', () => {
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
    sinon.assert.calledOnce(csvUtils.createCSVContent);
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
    sinon.assert.calledOnce(csvUtils.createCSVContent);
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
    sinon.assert.calledOnce(csvUtils.createCSVContent);
    sinon.assert.calledOnce(utils.checkIfIntervalPassed);
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
