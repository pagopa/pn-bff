const ssmUtils = require('./ssmParameter');
const s3Utils = require('./s3Utils');
const csvUtils = require('./csvUtils');
const cloudFrontUtils = require('./cloudFrontUtils');
const apiClient = require('./raddClient');
const utils = require('./utils');
const storeLocatorCsvEntity = require('./StoreLocatorCsvEntity');
const {
  wrongAddressesCsvHeader,
  wrongAddressesConfig,
} = require('../data/csvData');

exports.handleEvent = async () => {
  console.log('Handler invoked');
  ssmUtils.validateEnvironmentVariables();

  let forceGenerate = false;
  let sendToWebLanding = false;

  const malformedAddressS3Key = `${process.env.BFF_BUCKET_PREFIX}/malformed_addresses.csv`;
  const wrongPostalCodesS3Key = `${process.env.BFF_BUCKET_PREFIX}/wrong_postal_codes.csv`;
  const generationConfig = await ssmUtils.retrieveGenerationConfigParameter();

  if (generationConfig) {
    console.log('Configuration fetched:', generationConfig);
    forceGenerate = generationConfig.forceGenerate;
    sendToWebLanding = generationConfig.sendToWebLanding;
  }

  const csvConfiguration = await ssmUtils.retrieveCsvConfiguration();
  console.log('Configuration fetched:', csvConfiguration);

  const cafLocationIdsWhitelist =
    await ssmUtils.retrieveCafLocationIdsWhitelist();
  console.log('CAF whitelist configuration fetched', cafLocationIdsWhitelist);

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
  let wrongAddressesCsvContent = wrongAddressesCsvHeader;
  let wrongPostalCodesCsvContent = wrongAddressesCsvHeader;

  let lastKey = null;

  do {
    const apiResponse = await apiClient.fetchApi(lastKey, null);
    const registries = apiResponse.registries;
    console.log(
      'Fetched API registries response size:',
      apiResponse.registries.length
    );
    const records = registries.map((registry) =>
      storeLocatorCsvEntity.mapApiResponseToStoreLocatorCsvEntities(
        registry,
        cafLocationIdsWhitelist
      )
    );

    for (let record of records) {
      // Append to wrong addresses CSV file if the address is not valid
      if (!record.isRecordValid) {
        wrongAddressesCsvContent += csvUtils.createCSVContent(
          wrongAddressesConfig,
          [record.record]
        );
      }

      // Append to wrong postal codes CSV file if the postal code is not valid
      if (!record.isCAPValid) {
        wrongPostalCodesCsvContent += csvUtils.createCSVContent(
          wrongAddressesConfig,
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

  // Upload wrong postal codes CSV file
  await s3Utils.uploadVersionedFile(
    false,
    wrongPostalCodesS3Key,
    wrongPostalCodesCsvContent
  );

  // Upload store locator CSV file
  await s3Utils.uploadVersionedFile(
    sendToWebLanding,
    bffBucketS3Key,
    csvContent
  );

  // invalidate cache of the landing distribution
  if (sendToWebLanding) {
    const webLandingS3Key = s3Utils.generateS3Key(null, true);
    await cloudFrontUtils.invalidateCache(
      process.env.WEB_LANDING_DISTRIBUTION_ID,
      ['/' + webLandingS3Key]
    );
  }
};
