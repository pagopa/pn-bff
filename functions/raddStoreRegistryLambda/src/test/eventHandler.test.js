const { handler } = require('../app/eventHandler');
const { getLatestVersion } = require('../app/s3Utils');
const { fetchApi } = require('../app/raddClient');
const { validateCsvConfiguration, createCSVContent } = require('../app/csvUtils');
const { expect } = require('chai');
const { mockClient } = require('aws-sdk-client-mock');
const { retrieveGenerationConfigParameter, retrieveCsvConfiguration } = require('../app/ssmParameter');

const sinon = require('sinon');
const {
  S3Client,
  GetObjectCommand,
  PutObjectCommand,
  ListObjectVersionsCommand,
} = require('@aws-sdk/client-s3');
const { SSMClient, GetParameterCommand } = require('@aws-sdk/client-ssm');

let originalEnv;

beforeEach(() => {
  // Store the original environment
  originalEnv = process.env;

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
    GENERATE_INTERVAL: '0',
    RADD_STORE_REGISTRY_API_URL: 'http://0.0.0.0:3000/radd-net-private/api/v1/store',
    REGISTRY_PAGINATION_LIMIT: '1000',
    AWS_REGION: 'us-east-1',
    AWS_XRAY_CONTEXT_MISSING: 'IGNORE_ERROR',
    AWS_SECRET_ACCESS_KEY: 'TEST',
    AWS_ACCESS_KEY_ID: 'TEST',
    AWS_PROFILE_NAME: 'default',
    AWS_ENDPOINT_URL: 'http://localhost:4566/'
  };
});

afterEach(() => {
  // Restore the original environment
  process.env = originalEnv;
});

describe('handler', () => {
  it('generates new file when forceGenerate is true', async () => {

    const ssmMock = mockClient(SSMClient);
    ssmMock.on(GetParameterCommand).resolves({Parameter: {Value: `
    {
      "configurationVersion": "v1",
      "configs": [
        { "header": "descrizione", "field": "description" },
        { "header": "città", "field": "city" },
        { "header": "via", "field": "address" },
        { "header": "provincia", "field": "province" },
        { "header": "cap", "field": "zipCode" },
        { "header": "telefono", "field": "phoneNumber" },
        { "header": "lun", "field": "monday" },
        { "header": "mar", "field": "tuesday" },
        { "header": "mer", "field": "wednesday" },
        { "header": "gio", "field": "thursday" },
        { "header": "ven", "field": "friday" },
        { "header": "sab", "field": "saturday" },
        { "header": "dom", "field": "sunday" },
        { "header": "latitudine", "field": "latitude" },
        { "header": "longitudine", "field": "longitude" }
      ]
    }`}});

    const s3Mock = mockClient(S3Client);
    s3Mock.on(ListObjectVersionsCommand).resolves({
      Versions: [
        {
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
        }
      ]
    });


    const getLatestVersion = sinon.mock();
    getLatestVersion.once().returns( {
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

    const fetchApi = sinon.mock();
    fetchApi.once().returns({});
    const validateCsvConfiguration = sinon.mock();
    validateCsvConfiguration.once().returns();
    const createCSVContent = sinon.mock();
    createCSVContent.once().returns('')


    await handler({});

    expect(retrieveCsvConfiguration).toHaveBeenCalledTimes(1);
    expect(retrieveGenerationConfigParameter).toHaveBeenCalledTimes(1);
    expect(getLatestVersion).toHaveBeenCalledTimes(1);
    expect(fetchApi).toHaveBeenCalledTimes(1);
    expect(validateCsvConfiguration).toHaveBeenCalledTimes(1);
    expect(createCSVContent).toHaveBeenCalledTimes(1);
  });

  it('does not generate new file when forceGenerate is false and interval has not passed', async () => {
    getParameter.mockResolvedValueOnce(JSON.stringify({ forceGenerate: false }));
    getParameter.mockResolvedValueOnce(JSON.stringify({ configurationVersion: '1.0', configs: [] }));
    getLatestVersion.mockResolvedValue({ LastModified: new Date() });

    await handler({});

    expect(getParameter).toHaveBeenCalledTimes(2);
    expect(getLatestVersion).toHaveBeenCalledTimes(1);
    expect(fetchApi).not.toHaveBeenCalled();
    expect(validateCsvConfiguration).not.toHaveBeenCalled();
    expect(createCSVContent).not.toHaveBeenCalled();
  });
});