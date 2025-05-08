const ssmUtils = require('./ssmParameter');
const s3Utils = require('./s3Utils');
const csvUtils = require('./csvUtils');
const apiClient = require('./raddClient');
const utils = require('./utils');
const storeLocatorCsvEntity = require('./StoreLocatorCsvEntity');

exports.handleEvent = async () => {
  console.log('Handler invoked');
  validateEnvironmentVariables();

  let forceGenerate = false;
  let sendToWebLanding = false;

  const generationConfig = await ssmUtils.retrieveGenerationConfigParameter();

  if (generationConfig) {
    console.log('Configuration fetched:', generationConfig);
    forceGenerate = generationConfig.forceGenerate;
    sendToWebLanding = generationConfig.sendToWebLanding;
  }

  const csvConfiguration = await ssmUtils.retrieveCsvConfiguration();
  console.log('Configuration fetched:', csvConfiguration);

  const bffBucketS3Key = s3Utils.generateS3Key(
    csvConfiguration.configurationVersion,
    false
  );
  console.log('Generated S3 key:', bffBucketS3Key);

  const latestFile = await s3Utils.getLatestVersion(bffBucketS3Key);

  let generateFile = false;

  if (forceGenerate || !latestFile || utils.checkIfIntervalPassed(latestFile)) {
    generateFile = true;
  }

  if (generateFile) {
    console.log('Generating new file...');

    csvUtils.validateCsvConfiguration(csvConfiguration);

    const csvHeader = csvConfiguration.configs
      .map((conf) => conf.header)
      .join(';');
    let csvContent = csvHeader;

    let lastKey = null;
    do {
      const apiResponse = await apiClient.fetchApi(lastKey, null);
      console.log(
        'Fetched API registries response size:',
        apiResponse.registries.length
      );
      const records = apiResponse.registries.map((registry) =>
        storeLocatorCsvEntity.mapApiResponseToStoreLocatorCsvEntities(registry)
      );
      csvContent += csvUtils.createCSVContent(
        csvConfiguration.configs,
        records
      );
      lastKey = apiResponse.lastKey;
      console.log('Processed records, lastKey:', lastKey);
    } while (lastKey);

    await s3Utils.uploadVersionedFile(
      sendToWebLanding,
      bffBucketS3Key,
      csvContent
    );
  } else {
    console.log('No need to generate file.');
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
    'RADD_STORE_REGISTRY_API_URL',
    'AWS_LOCATION_REGION'
  ];

  requiredEnvVars.forEach((envVar) => {
    if (!process.env[envVar]) {
      console.error(`Missing required environment variable: ${envVar}`);
      throw new Error(`Missing required environment variable: ${envVar}`);
    } else {
      console.log(`Environment variable ${envVar} is set`);
    }
  });
}
