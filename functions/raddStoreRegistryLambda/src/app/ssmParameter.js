const axios = require('axios');

const retrieveGenerationConfigParameter = async () => {
  const generationConfigName =
    process.env.RADD_STORE_GENERATION_CONFIG_PARAMETER;
  console.log('Fetching generation config parameter:', generationConfigName);
  try {
    return JSON.parse(await getParameterFromLayer(generationConfigName));
  } catch (error) {
    console.error('Error retrieving SSM parameter:', error);
  }
};

const retrieveCsvConfiguration = async () => {
  const csvConfigParamName = process.env.CSV_CONFIGURATION_PARAMETER;
  console.log('Fetching configuration parameter:', csvConfigParamName);
  try {
    return JSON.parse(await getParameterFromLayer(csvConfigParamName));
  } catch (error) {
    console.error('Error retrieving SSM parameter:', error);
    throw error;
  }
};

async function getParameterFromLayer(parameterName) {
  try {
    const response = await axios.get(
      `http://localhost:2773/systemsmanager/parameters/get?name=${encodeURIComponent(
        parameterName
      )}`,
      {
        headers: {
          'X-Aws-Parameters-Secrets-Token': process.env.AWS_SESSION_TOKEN,
        },
      }
    );
    return response.data.Parameter.Value;
  } catch (err) {
    console.error('Unable to get SSM parameter ', err);
    throw new Error('Error retrieving SSM parameter');
  }
}

module.exports = {
  retrieveGenerationConfigParameter,
  retrieveCsvConfiguration,
};
