const { handler } = require('../app/eventHandler');
const s3Utils = require('../app/s3Utils');
const api = require('../app/raddClient');
const csvUtils = require('../app/csvUtils');
const ssmUtils = require('../app/ssmParameter');
const utils = require('../app/utils');
const sinon = require('sinon');
const assert = require('node:assert/strict');
let originalEnv;

function setupEnv() {
  // Mock the environment variables
  process.env = {
    ...originalEnv,
    BFF_BUCKET_NAME: 'radd-store-locator',
    BFF_BUCKET_PREFIX: 'pn-store-locator',
    WEB_LANDING_BUCKET_NAME: 'web-landing-store-locator',
    WEB_LANDING_BUCKET_PREFIX: 'web-landing',
    FILE_NAME: 'test-csv-file',
    CSV_CONFIGURATION_PARAMETER: '/pn-radd-store-registry-lambda/csv-configuration',
    RADD_STORE_GENERATION_CONFIG_PARAMETER: '/pn-radd-store-registry-lambda/send-to-web-landing',
    GENERATE_INTERVAL: '1',
    RADD_STORE_REGISTRY_API_URL: 'http://0.0.0.0:3000/radd-net-private/api/v1/store',
    REGISTRY_PAGINATION_LIMIT: '1000',
    AWS_REGION: 'us-east-1',
    AWS_XRAY_CONTEXT_MISSING: 'IGNORE_ERROR',
    AWS_SECRET_ACCESS_KEY: 'TEST',
    AWS_ACCESS_KEY_ID: 'TEST',
    AWS_PROFILE_NAME: 'default',
    AWS_ENDPOINT_URL: 'http://localhost:4566/'
  };
}

describe('handler generates new file', () => {
  beforeEach(() => {
    setupEnv();

    sinon.stub(ssmUtils, 'retrieveCsvConfiguration').resolves({
      configurationVersion: '1.0',
      configs: []
    });

    sinon.stub(api, 'fetchApi').resolves({
      registries: [],
      lastKey: null
    });

    
    sinon.stub(csvUtils, 'validateCsvConfiguration').returns();
    sinon.stub(csvUtils, 'createCSVContent').returns('');
    sinon.stub(s3Utils, 'uploadVersionedFile').returns();
  });

	afterEach(() => {
    sinon.restore();
  });

  it('generates new file when forceGenerate is true', async () => {
		sinon.stub(s3Utils, 'getLatestVersion').resolves(null);

    sinon.stub(ssmUtils, 'retrieveGenerationConfigParameter').resolves({ 
      forceGenerate: true, 
      sendToWebLanding: true 
    });

    sinon.stub(utils, 'checkIfIntervalPassed').returns(false);

    await handler({});

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

    await handler({});

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

    await handler({});

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
      configs: []
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
      sendToWebLanding: true 
    });

    sinon.stub(utils, 'checkIfIntervalPassed').returns(false);

    await handler({});

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
    await assert.rejects(handler({}), { message: 'Missing required environment variable: BFF_BUCKET_NAME' });
  });

});