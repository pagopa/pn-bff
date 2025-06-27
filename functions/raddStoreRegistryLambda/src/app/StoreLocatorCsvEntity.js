const { getCoordinatesForAddress } = require('./geocodeUtils');

class StoreLocatorCsvEntity {
  constructor() {
    this.description = '';
    this.city = '';
    this.address = '';
    this.awsAddress = '';
    this.province = '';
    this.region = '';
    this.zipCode = '';
    this.phoneNumber = '';
    this.monday = '';
    this.tuesday = '';
    this.wednesday = '';
    this.thursday = '';
    this.friday = '';
    this.saturday = '';
    this.sunday = '';
    this.cafOpeningHours = '';
    this.latitude = '';
    this.longitude = '';
  }

  setDescription(description) {
    if (description != null) this.description = sanitizeCSVField(description);
  }

  setCity(city) {
    if (city != null) this.city = sanitizeCSVField(city);
  }

  setAddress(address) {
    if (address != null) this.address = sanitizeCSVField(address);
  }

  setAwsAddress(awsAddress) {
    if (awsAddress != null) this.awsAddress = sanitizeCSVField(awsAddress);
  }

  setProvince(province) {
    if (province != null) this.province = sanitizeCSVField(province);
  }

  setRegion(region) {
    if (region != null) this.region = sanitizeCSVField(region);
  }

  setZipCode(zipCode) {
    if (zipCode != null) this.zipCode = sanitizeCSVField(zipCode);
  }

  setPhoneNumber(phoneNumber) {
    if (phoneNumber != null) this.phoneNumber = sanitizeCSVField(phoneNumber);
  }

  setMonday(monday) {
    if (monday != null) this.monday = sanitizeCSVField(monday);
  }

  setTuesday(tuesday) {
    if (tuesday != null) this.tuesday = sanitizeCSVField(tuesday);
  }

  setWednesday(wednesday) {
    if (wednesday != null) this.wednesday = sanitizeCSVField(wednesday);
  }

  setThursday(thursday) {
    if (thursday != null) this.thursday = sanitizeCSVField(thursday);
  }

  setFriday(friday) {
    if (friday != null) this.friday = sanitizeCSVField(friday);
  }

  setSaturday(saturday) {
    if (saturday != null) this.saturday = sanitizeCSVField(saturday);
  }

  setSunday(sunday) {
    if (sunday != null) this.sunday = sanitizeCSVField(sunday);
  }

  setCafOpeningHours(cafOpeningHours) {
    if (cafOpeningHours != null)
      this.cafOpeningHours = sanitizeCSVField(cafOpeningHours);
  }

  setLatitude(latitude) {
    if (latitude != null) this.latitude = sanitizeCSVField(latitude);
  }

  setLongitude(longitude) {
    if (longitude != null) this.longitude = sanitizeCSVField(longitude);
  }
}

const sanitizeCSVField = (field) => {
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
};

const getOpeningTimeByDay = (fullOpeningTime) => {
  const times = new Array(7).fill(null);
  if (fullOpeningTime) {
    const days = fullOpeningTime.split('#');
    for (let day of days) {
      switch (day.substring(0, 3).toUpperCase()) {
        case 'MON':
          times[0] = day.substring(4);
          break;
        case 'TUE':
          times[1] = day.substring(4);
          break;
        case 'WED':
          times[2] = day.substring(4);
          break;
        case 'THU':
          times[3] = day.substring(4);
          break;
        case 'FRI':
          times[4] = day.substring(4);
          break;
        case 'SAT':
          times[5] = day.substring(4);
          break;
        case 'SUN':
          times[6] = day.substring(4);
          break;
      }
    }
  }
  return times;
};

/**
 * returns an object with: storeRecord if awsScore is over the malformedAddressThreshold otherwise
 * returns a malformedRecord that will be added to malformed addresses CSV.
 */
const mapApiResponseToStoreLocatorCsvEntities = async (registry) => {
  const malformedAddressThreshold = Number(
    process.env.MALFORMED_ADDRESS_THRESHOLD
  );

  const storeLocatorCsvEntity = new StoreLocatorCsvEntity();

  storeLocatorCsvEntity.setDescription(registry.description);
  if (registry.address) {
    storeLocatorCsvEntity.setCity(registry.address.city);
    storeLocatorCsvEntity.setAddress(registry.address.addressRow);
    storeLocatorCsvEntity.setProvince(registry.address.pr);
    storeLocatorCsvEntity.setZipCode(registry.address.cap);
  }
  if (registry.phoneNumber) {
    storeLocatorCsvEntity.setPhoneNumber(
      registry.phoneNumber.replace(/\//g, ' ')
    );
  }

  if (registry.openingTime) {
    const formattedOpeningTime = getOpeningTimeByDay(registry.openingTime);
    if (formattedOpeningTime.every((el) => el === null)) {
      storeLocatorCsvEntity.setCafOpeningHours(registry.openingTime);
    }

    storeLocatorCsvEntity.setMonday(formattedOpeningTime[0]);
    storeLocatorCsvEntity.setTuesday(formattedOpeningTime[1]);
    storeLocatorCsvEntity.setWednesday(formattedOpeningTime[2]);
    storeLocatorCsvEntity.setThursday(formattedOpeningTime[3]);
    storeLocatorCsvEntity.setFriday(formattedOpeningTime[4]);
    storeLocatorCsvEntity.setSaturday(formattedOpeningTime[5]);
    storeLocatorCsvEntity.setSunday(formattedOpeningTime[6]);
  }

  try {
    const coordinatesResponse = await getCoordinatesForAddress(
      registry.address.addressRow,
      registry.address.pr,
      registry.address.cap,
      registry.address.city
    );

    if (
      coordinatesResponse &&
      coordinatesResponse.awsScore > malformedAddressThreshold &&
      coordinatesResponse.awsLatitude &&
      coordinatesResponse.awsLongitude
    ) {
      storeLocatorCsvEntity.setLatitude(coordinatesResponse.awsLatitude);
      storeLocatorCsvEntity.setLongitude(coordinatesResponse.awsLongitude);
      storeLocatorCsvEntity.setAwsAddress(coordinatesResponse.awsAddress);
      storeLocatorCsvEntity.setRegion(coordinatesResponse.awsAddressRegion);
    } else {
      return {
        storeRecord: null,
        malformedRecord: {
          ...storeLocatorCsvEntity,
          ...coordinatesResponse,
        },
      };
    }
  } catch (e) {
    console.log(e);
    return {
      storeRecord: null,
      malformedRecord: {
        ...storeLocatorCsvEntity,
      },
    };
  }

  return {
    storeRecord: storeLocatorCsvEntity,
    malformedRecord: null,
  };
};

module.exports = { mapApiResponseToStoreLocatorCsvEntities, sanitizeCSVField };
