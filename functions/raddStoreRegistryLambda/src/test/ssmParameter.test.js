const { mockClient } = require('aws-sdk-client-mock');
const { SSMClient, GetParameterCommand } = require('@aws-sdk/client-ssm');
const { retrieveCsvConfiguration, retrieveGenerationConfigParameter } = require('../app/ssmParameter');
const { expect } = require('chai');
const axios = require('axios');
const MockAdapter = require("axios-mock-adapter");
const assert = require('node:assert/strict');


describe('ssmParameter', () => {
    let ssmClientMock;
	beforeEach(() => {
        mock = new MockAdapter(axios);
        ssmClientMock = mockClient(SSMClient);
        process.env = {
            ...process.env,
            CSV_CONFIGURATION_PARAMETER: 'CSV_CONFIGURATION_PARAMETER',
            RADD_STORE_GENERATION_CONFIG_PARAMETER: 'RADD_STORE_GENERATION_CONFIG_PARAMETER'
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
        mock.onGet(url).reply(200, {Parameter: { Value: JSON.stringify(mockResponse)}}, {"Content-Type": "application/json"})
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
        mock.onGet(url).reply(200, {Parameter: { Value: JSON.stringify(mockResponse)}}, {"Content-Type": "application/json"})
        
        ssmClientMock.on(GetParameterCommand).resolves(
            { Parameter: { 
                Value: JSON.stringify(mockResponse) 
            } 
        });
       
        const response = await retrieveCsvConfiguration();
        expect(response).to.deep.equal(mockResponse);
    });

    it('should not retrieve csv configuration in case of exception', async () => {
        const parameterName = process.env.CSV_CONFIGURATION_PARAMETER;
        const url = `http://localhost:2773/systemsmanager/parameters/get?name=${encodeURIComponent(parameterName)}`;
        mock.onGet(url).reply(500);

        await assert.rejects(retrieveCsvConfiguration(), { message: "Error retrieving SSM parameter" });
    });
});