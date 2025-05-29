const ssmUtils = require('./ssmParameter');
const s3Utils = require('./s3Utils');
const csvUtils = require('./csvUtils');
const apiClient = require('./raddClient');
const utils = require('./utils');
const storeLocatorCsvEntity = require('./StoreLocatorCsvEntity');

const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

const REQUESTS_PER_SECOND =
  Number(process.env.AWS_LOCATION_REQUESTS_PER_SECOND) || 95;
const BATCH_SIZE = REQUESTS_PER_SECOND / 2;
const DELAY_MS = 1000 / (REQUESTS_PER_SECOND / BATCH_SIZE);

exports.handleEvent = async () => {
  console.log('Handler invoked');
  validateEnvironmentVariables();

  let forceGenerate = false;
  let sendToWebLanding = false;

  const generationConfig = await ssmUtils.retrieveGenerationConfigParameter();
  const malformedAddressS3Key = `${process.env.BFF_BUCKET_PREFIX}/malformed_addresses.csv`;

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

  const shouldGenerateFile =
    forceGenerate || !latestFile || utils.checkIfIntervalPassed(latestFile);

  if (!shouldGenerateFile) {
    console.log('No need to generate file.');
    return;
  }

  console.log('Generating new file...');

  csvUtils.validateCsvConfiguration(csvConfiguration);

  const csvHeader = csvConfiguration.configs
    .map((conf) => conf.header)
    .join(';');
  let csvContent = csvHeader;
  let wrongAddressesCsvContent = csvUtils.wrongAddressesCsvHeader;

  let lastKey = null;

  do {
    const apiResponse = await apiClient.fetchApi(lastKey, null);
    const registries = apiResponse.registries;
    console.log(
      'Fetched API registries response size:',
      apiResponse.registries.length
    );

    for (let i = 0; i < registries.length; i += BATCH_SIZE) {
      const batch = registries.slice(i, i + BATCH_SIZE);

      const recordPromises = batch.map((registry) =>
        storeLocatorCsvEntity.mapApiResponseToStoreLocatorCsvEntities(registry)
      );

      const results = await Promise.all(recordPromises);

      for (let result of results) {
        if (result.malformedRecord) {
          wrongAddressesCsvContent += csvUtils.createCSVContent(
            csvConfiguration.wrongAddressesConfig,
            [result.malformedRecord]
          );
        }
        if (result.storeRecord) {
          csvContent += csvUtils.createCSVContent(csvConfiguration.configs, [
            result.storeRecord,
          ]);
        }
      }

      if (i + BATCH_SIZE < registries.length || apiResponse.lastKey) {
        console.log(`Sleep for ${DELAY_MS}`);
        await sleep(DELAY_MS);
      }
    }

    lastKey = apiResponse.lastKey;
    console.log('Processed records, lastKey:', lastKey);
  } while (lastKey);

  await s3Utils.uploadVersionedFile(
    sendToWebLanding,
    bffBucketS3Key,
    csvContent
  );

  await s3Utils.uploadVersionedFile(
    false,
    malformedAddressS3Key,
    wrongAddressesCsvContent
  );
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
    'AWS_LOCATION_REGION',
    'AWS_LOCATION_REQUESTS_PER_SECOND',
    'MALFORMED_ADDRESS_THRESHOLD',
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
