const { retrieveGenerationConfigParameter, retrieveCsvConfiguration } = require('./ssmParameter');
const { getLatestVersion, uploadFile, copyObject, generateS3Key } = require('./s3Utils');
const { validateCsvConfiguration, createCSVContent} = require('./csvUtils');
const { fetchApi } = require('./raddClient');
const { checkIfIntervalPassed } = require('./utils');
const { mapApiResponseToStoreLocatorCsvEntities } = require('./StoreLocatorCsvEntity');

exports.handler = async (event) => {
  console.log('Handler invoked with event:', event);
  validateEnvironmentVariables();

  let forceGenerate = false
  let sendToWebLanding = false

  const generationConfig = retrieveGenerationConfigParameter();

  if(generationConfig){
    console.log('Configuration fetched:', generationConfig);
    forceGenerate = generationConfig.forceGenerate;
    sendToWebLanding = generationConfig.sendToWebLanding;
  }

  const csvConfiguration = retrieveCsvConfiguration();
  console.log('Configuration fetched:', csvConfiguration);

  const bffBucketS3Key = generateS3Key(csvConfiguration.configurationVersion, false);
  console.log('Generated S3 key:', bffBucketS3Key);

  const latestFile = await getLatestVersion(bffBucketS3Key);
  console.log('Latest file:', latestFile);

  let generateFile = false;

  if (forceGenerate || !latestFile || checkIfIntervalPassed(latestFile)) {
    generateFile = true;
  }

  if (generateFile) {
    console.log('Generating new file...');

    validateCsvConfiguration(csvConfiguration);

    const csvHeader = csvConfiguration.configs.map(conf => conf.header).join(',');
    let csvContent = csvHeader + '\n';

    let lastKey = null;
    do {
      const apiResponse = await fetchApi(lastKey, null);
      console.log('Fetched API registries response size:', apiResponse.registries.length);
      const records = apiResponse.registries.map(registry => mapApiResponseToStoreLocatorCsvEntities(registry));
      csvContent += createCSVContent(csvConfiguration.configs, records);
      lastKey = apiResponse.lastKey;
      console.log('Processed records, lastKey:', lastKey);
    } while (lastKey);

    //TODO: ADD UPLOAD ON S3

  } else {
    console.log("No need to generate file.");
  }
};

function validateEnvironmentVariables() {
  const requiredEnvVars = [
    'BFF_BUCKET_NAME',
    'BFF_BUCKET_PREFIX',
    'WEB_LANDING_BUCKET_NAME',
    'WEB_LANDING_BUCKET_PREFIX',
    'FILE_NAME',
    'CSV_CONFIGURATION_PARAMETER',
    'GENERATE_INTERVAL',
    'RADD_STORE_GENERATION_CONFIG_PARAMETER',
    'RADD_STORE_REGISTRY_API_URL'
  ];

  requiredEnvVars.forEach(envVar => {
    if (!process.env[envVar]) {
      console.error(`Missing required environment variable: ${envVar}`);
      throw new Error(`Missing required environment variable: ${envVar}`);
    } else {
      console.log(`Environment variable ${envVar} is set`);
    }
  });
}



