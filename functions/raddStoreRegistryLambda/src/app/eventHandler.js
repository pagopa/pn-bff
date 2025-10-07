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

  const malformedAddressS3Key = `${process.env.BFF_BUCKET_PREFIX}/malformed_addresses.csv`;
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
    const records = registries.map((registry) =>
      storeLocatorCsvEntity.mapApiResponseToStoreLocatorCsvEntities(registry)
    );

    for (let record of records) {
      if (!record.isRecordValid) {
        wrongAddressesCsvContent += csvUtils.createCSVContent(
          csvUtils.wrongAddressesConfig,
          [record.record]
        );
      }

      csvContent += csvUtils.createCSVContent(csvConfiguration.configs, [
        record.record,
      ]);
    }

    lastKey = apiResponse.lastKey;
    console.log('Processed records, lastKey:', lastKey);
  } while (lastKey);

  // Upload malformed addresses CSV file
  await s3Utils.uploadVersionedFile(
    false,
    malformedAddressS3Key,
    wrongAddressesCsvContent
  );

  // Upload store locator CSV file
  await s3Utils.uploadVersionedFile(
    sendToWebLanding,
    bffBucketS3Key,
    csvContent
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
    'SUBREGION_THRESHOLD',
    'LOCALITY_THRESHOLD',
    'POSTAL_CODE_THRESHOLD',
    'ADDRESS_NUMBER_THRESHOLD',
    'OVERALL_THRESHOLD',
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
