const { SSMClient, GetParameterCommand } = require('@aws-sdk/client-ssm');

const client = new SSMClient({ region: process.env.AWS_REGION });

const retrieveGenerationConfigParameter = () => {
  const generationConfigName = process.env.RADD_STORE_GENERATION_CONFIG_PARAMETER;
  console.log('Fetching generation config parameter:', generationConfigName);
  try{
    const generationConfig = await getParameter(generationConfigName);
    return JSON.parse(generationConfig);
  } catch (error) {
    console.error('Error retrieving SSM parameter:', error);
  }
}

const retrieveCsvConfiguration = () => {
  const csvConfigParamName = process.env.CSV_CONFIGURATION_PARAMETER;
  console.log('Fetching configuration parameter:', csvConfigParamName);
  try{
    return JSON.parse(await getParameter(csvConfigParamName, true));
  } catch (error) {
    console.error('Error retrieving SSM parameter:', error);
    throw error;
  }
}

const getParameter = async (parameterName) => {
    console.log(`Retrieving parameter: ${parameterName}`);
    const command = new GetParameterCommand({ Name: parameterName });
    const response = await client.send(command);
    console.log(`Successfully retrieved parameter: ${parameterName}`);
    return response.Parameter.Value;
}

module.exports = { getParameter, retrieveGenerationConfig };
