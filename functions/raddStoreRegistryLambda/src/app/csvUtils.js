const validFieldValue = [
  'description',
  'city',
  'address',
  'normalizedAddress',
  'province',
  'zipCode',
  'phoneNumbers',
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

const getOpeningTimeByDay = (openingTimeObj) => {
  const times = new Array(7).fill(null);

  const dayMapping = {
    lun: 0,
    mar: 1,
    mer: 2,
    gio: 3,
    ven: 4,
    sab: 5,
    dom: 6,
  };

  for (const [dayKey, hours] of Object.entries(openingTimeObj)) {
    const dayIndex = dayMapping[dayKey.toLowerCase()];
    if (dayIndex !== undefined) {
      times[dayIndex] = hours;
    }
  }

  return times;
};
function sanitizeCSVField(field) {
  if (field == null) return '';

  const fieldStr = String(field).trim();

  if (fieldStr === '') return '';

  const cleanedField = fieldStr
    .replaceAll('\n', ' ')
    .replaceAll('\r', ' ')
    .replaceAll('\t', ' ')
    .replaceAll(/\s+/g, ' ');

  const escapedField = cleanedField.replaceAll('"', '""');

  return `"${escapedField}"`;
}

module.exports = {
  validateCsvConfiguration,
  createCSVContent,
  getOpeningTimeByDay,
  sanitizeCSVField,
};
