const { sanitizeCSVField } = require('./csvUtils');
const {
  getOpeningTimeByDay,
  isAWSAddressValid,
} = require('../utils/storeLocatorUtils');

const createStoreLocatorEntity = (data) => ({
  locationId: sanitizeCSVField(data.locationId),
  partnerId: sanitizeCSVField(data.partnerId),
  externalCodes: sanitizeCSVField(data.externalCodes),
  description: sanitizeCSVField(data.description),
  city: sanitizeCSVField(data.city),
  cafAddress: sanitizeCSVField(data.cafAddress),
  normalizedAddress: sanitizeCSVField(data.normalizedAddress),
  address: sanitizeCSVField(data.address),
  province: sanitizeCSVField(data.province),
  zipCode: sanitizeCSVField(data.zipCode),
  phoneNumbers: sanitizeCSVField(data.phoneNumbers),
  monday: sanitizeCSVField(data.monday),
  tuesday: sanitizeCSVField(data.tuesday),
  wednesday: sanitizeCSVField(data.wednesday),
  thursday: sanitizeCSVField(data.thursday),
  friday: sanitizeCSVField(data.friday),
  saturday: sanitizeCSVField(data.saturday),
  sunday: sanitizeCSVField(data.sunday),
  rawOpeningHours: sanitizeCSVField(data.rawOpeningHours),
  latitude: sanitizeCSVField(data.latitude),
  longitude: sanitizeCSVField(data.longitude),
  email: sanitizeCSVField(data.email),
  website: sanitizeCSVField(data.website),
  appointmentRequired: sanitizeCSVField(data.appointmentRequired),
  biasPoint: data.biasPoint,
});

const formatExternalCodes = (externalCodes) => {
  if (!externalCodes?.length) return undefined;
  return externalCodes.join(' , ').replace(/\//g, ' ');
};

const formatPhoneNumbers = (phoneNumbers) => {
  if (!phoneNumbers?.length) return undefined;
  return phoneNumbers.join('_').replace(/\//g, ' ');
};

const formatCafAddress = (address) => {
  if (!address) return undefined;
  return `${address.addressRow}, ${address.cap} ${address.city}`;
};

const getOpeningHours = (openingTime) => {
  if (!openingTime) return {};

  if (typeof openingTime === 'object' && openingTime !== null) {
    const formattedOpeningTime = getOpeningTimeByDay(openingTime);
    return {
      monday: formattedOpeningTime[0],
      tuesday: formattedOpeningTime[1],
      wednesday: formattedOpeningTime[2],
      thursday: formattedOpeningTime[3],
      friday: formattedOpeningTime[4],
      saturday: formattedOpeningTime[5],
      sunday: formattedOpeningTime[6],
    };
  }

  if (typeof openingTime === 'string') {
    return { rawOpeningHours: openingTime };
  }

  return {};
};

const getAppointmentRequired = (appointmentRequired) => {
  if (appointmentRequired === null || appointmentRequired === undefined)
    return undefined;
  return appointmentRequired ? 'si' : 'no';
};

const selectAddressFields = (normalizedAddress, address, isValid) => {
  if (!normalizedAddress || !address) return {};

  return {
    address: isValid ? normalizedAddress.addressRow : formatCafAddress(address),
    city: isValid ? normalizedAddress.city : address.city,
    province: isValid ? normalizedAddress.province : address.province,
    zipCode: isValid ? normalizedAddress.cap : address.cap,
  };
};

/**
 * returns an object with record and isRecordValid properties.
 * record is the StoreLocatorCsvEntity created from the registry
 * isRecordValid is a boolean indicating if the record is valid (based on the AWS address validation)
 *
 * @param registry - The raw registry to be mapped into a StoreLocatorCsvEntity
 */
const mapApiResponseToStoreLocatorCsvEntities = (registry) => {
  const isNormalizedAddressValid = isAWSAddressValid(
    registry.normalizedAddress?.biasPoint
  );
  const isCAPValid =
    registry?.normalizedAddress?.biasPoint?.postalCode >=
    parseFloat(process.env.POSTAL_CODE_THRESHOLD);

  const data = {
    locationId: registry?.locationId,
    partnerId: registry?.partnerId,
    externalCodes: formatExternalCodes(registry?.externalCodes),
    description: registry?.description,
    cafAddress: formatCafAddress(registry?.address),
    normalizedAddress: registry?.normalizedAddress?.addressRow,
    latitude: registry?.normalizedAddress?.latitude,
    longitude: registry?.normalizedAddress?.longitude,
    phoneNumbers: formatPhoneNumbers(registry?.phoneNumbers),
    email: registry?.email,
    website: registry?.website,
    appointmentRequired: getAppointmentRequired(registry?.appointmentRequired),
    biasPoint: registry?.normalizedAddress?.biasPoint
      ? JSON.stringify(registry.normalizedAddress.biasPoint)
      : '',
    ...selectAddressFields(
      registry?.normalizedAddress,
      registry?.address,
      isNormalizedAddressValid
    ),
    ...getOpeningHours(registry?.openingTime),
  };

  const record = createStoreLocatorEntity(data);

  return {
    record,
    isRecordValid: isNormalizedAddressValid,
    isCAPValid,
  };
};

module.exports = {
  mapApiResponseToStoreLocatorCsvEntities,
  sanitizeCSVField,
};
