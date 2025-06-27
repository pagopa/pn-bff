const validFieldValue = [
  'description',
  'city',
  'address',
  'awsAddress',
  'province',
  'region',
  'zipCode',
  'phoneNumber',
  'monday',
  'tuesday',
  'wednesday',
  'thursday',
  'friday',
  'saturday',
  'sunday',
  'latitude',
  'longitude',
  'cafOpeningHours',
];

const wrongAddressesConfig = [
  { header: 'descrizione', field: 'description' },
  { header: 'indirizzo', field: 'address' },
  { header: 'citta', field: 'city' },
  { header: 'provincia', field: 'province' },
  { header: 'indirizzo AWS', field: 'awsAddress' },
  { header: 'score AWS', field: 'awsScore' },
  { header: 'latitudine', field: 'awsLatitude' },
  { header: 'longitudine', field: 'awsLongitude' },
];

const wrongAddressesCsvHeader = wrongAddressesConfig
  .map((config) => config.header)
  .join(';');

function validateCsvConfiguration(csvConfiguration) {
  console.log('Validating configuration');
  if (!csvConfiguration) throw new Error('Configuration is missing');
  if (!csvConfiguration.configurationVersion)
    throw new Error('configurationVersion is missing');
  if (!Array.isArray(csvConfiguration.configs))
    throw new Error('configs should be an array');
  csvConfiguration.configs.forEach((conf, index) => {
    if (!conf.header)
      throw new Error(
        `Header is missing in csvConfiguration at index ${index}`
      );
    if (!validFieldValue.includes(conf.field))
      console.warn(`Invalid field "${conf.field}" for header "${conf.header}"`);
  });

  console.log('Configuration is valid');
}

function createCSVContent(configs, data) {
  console.log('Creating CSV content');
  let csvContent = '';
  data.forEach((record) => {
    csvContent += '\n';
    const row = configs
      .map((conf) => (conf.field ? record[conf.field] || '' : ''))
      .join(';');
    csvContent += row;
  });
  console.log('CSV content created successfully');
  return csvContent;
}

module.exports = {
  validateCsvConfiguration,
  createCSVContent,
  wrongAddressesCsvHeader,
  wrongAddressesConfig,
};
