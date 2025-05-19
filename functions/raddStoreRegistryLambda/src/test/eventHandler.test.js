const { handleEvent } = require('../app/eventHandler');
const s3Utils = require('../app/s3Utils');
const api = require('../app/raddClient');
const csvUtils = require('../app/csvUtils');
const ssmUtils = require('../app/ssmParameter');
const utils = require('../app/utils');
const sinon = require('sinon');
const assert = require('node:assert/strict');

function setupEnv() {
  // Mock the environment variables
  process.env = {
    BFF_BUCKET_NAME: 'radd-store-locator',
    BFF_BUCKET_PREFIX: 'pn-store-locator',
    WEB_LANDING_BUCKET_NAME: 'web-landing-store-locator',
    WEB_LANDING_BUCKET_PREFIX: 'web-landing',
    FILE_NAME: 'test-csv-file',
    CSV_CONFIGURATION_PARAMETER:
      '/pn-radd-store-registry-lambda/csv-configuration',
    RADD_STORE_GENERATION_CONFIG_PARAMETER:
      '/pn-radd-store-registry-lambda/send-to-web-landing',
    GENERATE_INTERVAL: '1',
    RADD_STORE_REGISTRY_API_URL:
      'http://0.0.0.0:3000/radd-net-private/api/v1/store',
    REGISTRY_PAGINATION_LIMIT: '1000',
    AWS_REGION: 'us-east-1',
    AWS_XRAY_CONTEXT_MISSING: 'IGNORE_ERROR',
    AWS_SECRET_ACCESS_KEY: 'TEST',
    AWS_ACCESS_KEY_ID: 'TEST',
    AWS_PROFILE_NAME: 'default',
    AWS_ENDPOINT_URL: 'http://localhost:4566/',
    AWS_LOCATION_REGION: 'eu-central-1',
    AWS_LOCATION_REQUESTS_PER_SECOND: '95',
  };
}

describe('handler generates new file', () => {
  beforeEach(() => {
    setupEnv();

    sinon.stub(ssmUtils, 'retrieveCsvConfiguration').resolves({
      configurationVersion: '1.0',
      configs: [],
    });

    sinon.stub(api, 'fetchApi').resolves({
      registries: Array(10).fill({ id: 'test' }),
      lastKey: null,
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

  it('properly handles batching and sleep timing between batches', async () => {
    sinon.stub(s3Utils, 'getLatestVersion').resolves(null);
    sinon.stub(ssmUtils, 'retrieveGenerationConfigParameter').resolves({
      forceGenerate: true,
      sendToWebLanding: true,
    });

    const firstBatch = {
      registries: Array(5).fill({ id: 'batch1' }),
      lastKey: 'next',
    };
    const secondBatch = {
      registries: Array(5).fill({ id: 'batch2' }),
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
    sinon.assert.calledTwice(csvUtils.createCSVContent);

    clock.restore();
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
