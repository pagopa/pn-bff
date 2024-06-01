const { mockClient } = require('aws-sdk-client-mock');
const { SSMClient, GetParameterCommand } = require('@aws-sdk/client-ssm');
const { retrieveCsvConfiguration, retrieveGenerationConfigParameter } = require('../app/ssmParameter');
const { expect } = require('chai');
const assert = require('node:assert/strict');

describe('ssmParameter', () => {
    let ssmClientMock;
	beforeEach(() => {
        ssmClientMock = mockClient(SSMClient);
        process.env = {
            ...process.env,
            CSV_CONFIGURATION_PARAMETER: 'CSV_CONFIGURATION_PARAMETER',
            RADD_STORE_GENERATION_CONFIG_PARAMETER: 'RADD_STORE_GENERATION_CONFIG_PARAMETER'
        };
    });

    afterEach(() => {
        ssmClientMock.restore();
    });

    it('should retrieve generation config parameter', async () => {
        const mockResponse = { forceGenerate: true, sendToWebLanding: true };
        ssmClientMock.on(GetParameterCommand).resolves({ 
            Parameter: { 
                Value: JSON.stringify(mockResponse)
            } 
        });
        const response = await retrieveGenerationConfigParameter();
        expect(response).to.deep.equal(mockResponse);
    });

    it('should not retrieve generation config parameter in case of exeption', async () => {
        ssmClientMock.on(GetParameterCommand).rejects(new Error('Error retrieving SSM parameter'));

        const response = await retrieveGenerationConfigParameter();
        expect(response).equal(undefined);
    });

    it('should retrieve csv configuration', async () => {
        const mockResponse = { configurationVersion: '1.0', configs: [] };
        ssmClientMock.on(GetParameterCommand).resolves(
            { Parameter: { 
                Value: JSON.stringify(mockResponse) 
            } 
        });
       
        const response = await retrieveCsvConfiguration();
        expect(response).to.deep.equal(mockResponse);
    });

    it('should not retrieve csv configuration in case of exception', async () => {
        // Mocking the SSMClient to throw an error
        ssmClientMock.on(GetParameterCommand).rejects(new Error('Error retrieving SSM parameter'));

        await assert.rejects(retrieveCsvConfiguration(), { message: 'Error retrieving SSM parameter' });
    });
});