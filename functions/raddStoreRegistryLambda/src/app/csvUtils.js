const validFieldValue = [
  'locationId',
  'partnerId',
  'externalCodes',
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
  'rawOpeningHours',
  'email',
  'website',
  'appointmentRequired',
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

/**
 * Check if the AWS address is valid based on scores and thresholds
 *
 * @param {Object} scores - AWS address scores object (subRegion, locality, postalCode, addressNumber, overall)
 * @returns {boolean} True if address is valid, false otherwise
 */
function isAWSAddressValid(scores) {
  if (!scores) return false;

  const requiredScores = [
    'subRegion',
    'locality',
    'postalCode',
    'addressNumber',
  ];

  const thresholds = {
    subRegion: parseFloat(process.env.SUBREGION_THRESHOLD),
    locality: parseFloat(process.env.LOCALITY_THRESHOLD),
    postalCode: parseFloat(process.env.POSTALCODE_THRESHOLD),
    addressNumber: parseFloat(process.env.ADDRESSNUMBER_THRESHOLD),
    overall: parseFloat(process.env.OVERALL_THRESHOLD),
  };

  // Check all required fields exist and are truthy
  const hasAllRequiredFields = requiredScores.every(
    (key) => scores[key] != null && scores[key] !== false
  );

  if (!hasAllRequiredFields || scores.overall == null) {
    return false;
  }

  // Check if all required scores and overall are equal to 1
  const allScoresArePerfect = requiredScores.every((key) => scores[key] === 1);
  if (allScoresArePerfect && scores.overall === 1) {
    return true;
  }

  // If all required scores are perfect but overall is less than threshold, return false
  if (allScoresArePerfect && scores.overall < thresholds.overall) {
    return false;
  }

  // Check if thresholds are equal or greater than minimum thresholds
  const areThresholdsOk = requiredScores.every(
    (key) => scores[key] >= thresholds[key]
  );

  return areThresholdsOk && scores.overall >= thresholds.overall;
}

module.exports = {
  validateCsvConfiguration,
  createCSVContent,
  getOpeningTimeByDay,
  sanitizeCSVField,
  isAWSAddressValid,
};
