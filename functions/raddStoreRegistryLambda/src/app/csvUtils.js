const fs = require('fs');
const { promisify } = require('util');
const writeFile = promisify(fs.writeFile);

const validFieldValue = [
  "description", "city", "address", "province", "zipCode",
  "phoneNumber", "monday", "tuesday", "wednesday", "thursday",
  "friday", "saturday", "sunday", "latitude", "longitude"
];

function validateCsvConfiguration(csvConfiguration) {
  console.log('Validating configuration');
  if (!csvConfiguration) throw new Error('Configuration is missing');
  if (!csvConfiguration.configurationVersion) throw new Error('configurationVersion is missing');
  if (!Array.isArray(csvConfiguration.configs)) throw new Error('configs should be an array');
  csvConfiguration.configs.forEach((conf, index) => {
    if (!conf.header) throw new Error(`Header is missing in csvConfiguration at index ${index}`);
    if (!validFieldValue.includes(conf.field)) console.warn(`Invalid field "${conf.field}" for header "${conf.header}"`);
  });

  console.log('Configuration is valid');
}

function createCSVContent(configs, data) {
  console.log('Creating CSV content');
  let csvContent = '';
  data.forEach((record, index) => {
    const row = configs.map(conf => conf.field ? record[conf.field] || '' : '').join(';');
    csvContent += row + '\n';
    console.log(`Processed record ${index + 1}`);
  });
  console.log('CSV content created successfully');
  return csvContent;
}

module.exports = { validateCsvConfiguration, createCSVContent};
