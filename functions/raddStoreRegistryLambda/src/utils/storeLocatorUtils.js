/**
 * Convert opening time object to an array of opening times for each day of the week
 * @param {*} openingTimeObj - Object with keys as day abbreviations and values as opening hours
 * @returns {Array} Array of opening hours for each day of the week (0=Monday, 6=Sunday)
 */
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
    postalCode: parseFloat(process.env.POSTAL_CODE_THRESHOLD),
    addressNumber: parseFloat(process.env.ADDRESS_NUMBER_THRESHOLD),
    overall: parseFloat(process.env.OVERALL_THRESHOLD),
  };

  return (
    requiredScores.every(
      (field) => scores[field] != null && scores[field] >= thresholds[field]
    ) && scores.overall >= thresholds.overall
  );
}

module.exports = {
  getOpeningTimeByDay,
  isAWSAddressValid,
};
