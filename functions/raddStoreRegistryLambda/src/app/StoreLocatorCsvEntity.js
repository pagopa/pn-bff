class StoreLocatorCsvEntity {
  constructor() {
    this.description = '';
    this.city = '';
    this.address = '';
    this.province = '';
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

  setProvince(province) {
    if (province != null) this.province = sanitizeCSVField(province);
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
 * Returns a StoreLocatorCsvEntity instance if the awsScore exceeds the malformedAddressThreshold
 * Otherwise, the address will be skipped
 *
 * @param registry - The raw registry to be mapped into a StoreLocatorCsvEntity
 */
const mapApiResponseToStoreLocatorCsvEntities = async (registry) => {
  const malformedAddressThreshold = Number(
    process.env.MALFORMED_ADDRESS_THRESHOLD
  );

  if (
    !registry.normalizedAddress.biasPoint?.overall ||
    registry.normalizedAddress.biasPoint.overall < malformedAddressThreshold
  ) {
    return;
  }

  const storeLocatorCsvEntity = new StoreLocatorCsvEntity();

  storeLocatorCsvEntity.setDescription(registry.description);
  if (registry.normalizedAddress) {
    storeLocatorCsvEntity.setCity(registry.normalizedAddress.city);
    storeLocatorCsvEntity.setAddress(registry.normalizedAddress.addressRow);
    storeLocatorCsvEntity.setProvince(registry.normalizedAddress.province);
    storeLocatorCsvEntity.setZipCode(registry.normalizedAddress.cap);

    if (
      registry.normalizedAddress.latitude &&
      registry.normalizedAddress.longitude
    ) {
      storeLocatorCsvEntity.setLatitude(registry.normalizedAddress.latitude);
      storeLocatorCsvEntity.setLongitude(registry.normalizedAddress.longitude);
    }
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

  return storeLocatorCsvEntity;
};

module.exports = { mapApiResponseToStoreLocatorCsvEntities, sanitizeCSVField };
