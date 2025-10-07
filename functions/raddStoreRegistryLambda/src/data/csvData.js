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

const wrongAddressesConfig = [
  { header: 'locationId', field: 'locationId' },
  { header: 'descrizione', field: 'description' },
  { header: 'indirizzo_originale', field: 'cafAddress' },
  { header: 'indirizzo_normalizzato', field: 'normalizedAddress' },
  { header: 'score_AWS', field: 'biasPoint' },
];

const wrongAddressesCsvHeader = wrongAddressesConfig
  .map((config) => config.header)
  .join(';');

module.exports = {
  validFieldValue,
  wrongAddressesConfig,
  wrongAddressesCsvHeader,
};
