const { mockClient } = require('aws-sdk-client-mock');
const { SSMClient, GetParameterCommand } = require('@aws-sdk/client-ssm');
const {
  retrieveCsvConfiguration,
  retrieveGenerationConfigParameter,
} = require('../app/ssmParameter');
const { expect } = require('chai');
const axios = require('axios');
const MockAdapter = require('axios-mock-adapter');
const assert = require('node:assert/strict');

describe('ssmParameter', () => {
  let ssmClientMock;
  beforeEach(() => {
    mock = new MockAdapter(axios);
    ssmClientMock = mockClient(SSMClient);
    process.env = {
      ...process.env,
      CSV_CONFIGURATION_PARAMETER: 'CSV_CONFIGURATION_PARAMETER',
      RADD_STORE_GENERATION_CONFIG_PARAMETER:
        'RADD_STORE_GENERATION_CONFIG_PARAMETER',
    };
  });

  afterEach(() => {
    mock.reset();
    ssmClientMock.restore();
  });

  it('should retrieve generation config parameter', async () => {
    const mockResponse = { forceGenerate: true, sendToWebLanding: true };
    const parameterName = process.env.RADD_STORE_GENERATION_CONFIG_PARAMETER;
    const url = `http://localhost:2773/systemsmanager/parameters/get?name=${encodeURIComponent(parameterName)}`;
    mock
      .onGet(url)
      .reply(
        200,
        { Parameter: { Value: JSON.stringify(mockResponse) } },
        { 'Content-Type': 'application/json' }
      );
    const response = await retrieveGenerationConfigParameter();
    expect(response).to.deep.equal(mockResponse);
  });

  it('should not retrieve generation config parameter in case of exeption', async () => {
    const parameterName = process.env.RADD_STORE_GENERATION_CONFIG_PARAMETER;
    const url = `http://localhost:2773/systemsmanager/parameters/get?name=${encodeURIComponent(parameterName)}`;
    mock.onGet(url).reply(500);

    const response = await retrieveGenerationConfigParameter();
    expect(response).equal(undefined);
  });

  it('should retrieve csv configuration', async () => {
    const mockResponse = { configurationVersion: '1.0', configs: [] };
    const parameterName = process.env.CSV_CONFIGURATION_PARAMETER;
    const url = `http://localhost:2773/systemsmanager/parameters/get?name=${encodeURIComponent(parameterName)}`;
    mock
      .onGet(url)
      .reply(
        200,
        { Parameter: { Value: JSON.stringify(mockResponse) } },
        { 'Content-Type': 'application/json' }
      );

    ssmClientMock.on(GetParameterCommand).resolves({
      Parameter: {
        Value: JSON.stringify(mockResponse),
      },
    });

    const response = await retrieveCsvConfiguration();
    expect(response).to.deep.equal(mockResponse);
  });

  it('should not retrieve csv configuration in case of exception', async () => {
    const parameterName = process.env.CSV_CONFIGURATION_PARAMETER;
    const url = `http://localhost:2773/systemsmanager/parameters/get?name=${encodeURIComponent(parameterName)}`;
    mock.onGet(url).reply(500);

    await assert.rejects(retrieveCsvConfiguration(), {
      message: 'Error retrieving SSM parameter',
    });
  });

  it('should retrive whitelist configuration and split it to array', async () => {
    const parameterName = process.env.CAF_LOCATION_IDS_WHITELIST_PARAMETER;
    const url = `http://localhost:2773/systemsmanager/parameters/get?name=${encodeURIComponent(parameterName)}`;
    const mockResponse = 'LOC-123, LOC-456 ,LOC-789';
    mock
      .onGet(url)
      .reply(
        200,
        { Parameter: { Value: mockResponse } },
        { 'Content-Type': 'application/json' }
      );

    const { retrieveCafLocationIdsWhitelist } = require('../app/ssmParameter');
    const response = await retrieveCafLocationIdsWhitelist();
    expect(response).to.deep.equal(['LOC-123', 'LOC-456', 'LOC-789']);
  });

  it('should retrive empty array if whitelist configuration is empty', async () => {
    const parameterName = process.env.CAF_LOCATION_IDS_WHITELIST_PARAMETER;
    const url = `http://localhost:2773/systemsmanager/parameters/get?name=${encodeURIComponent(parameterName)}`;
    const mockResponse = '';
    mock
      .onGet(url)
      .reply(
        200,
        { Parameter: { Value: mockResponse } },
        { 'Content-Type': 'application/json' }
      );

    const { retrieveCafLocationIdsWhitelist } = require('../app/ssmParameter');
    const response = await retrieveCafLocationIdsWhitelist();
    expect(response).to.deep.equal([]);
  });

  it('should not retrieve whitelist configuration in case of exception', async () => {
    const parameterName = process.env.CAF_LOCATION_IDS_WHITELIST_PARAMETER;
    const url = `http://localhost:2773/systemsmanager/parameters/get?name=${encodeURIComponent(parameterName)}`;
    mock.onGet(url).reply(500);

    const { retrieveCafLocationIdsWhitelist } = require('../app/ssmParameter');
    const response = await retrieveCafLocationIdsWhitelist();
    expect(response).to.equal(undefined);
  });
});
