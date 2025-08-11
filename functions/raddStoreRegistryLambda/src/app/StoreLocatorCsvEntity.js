class StoreLocatorCsvEntity {
  constructor() {
    this.description = '';
    this.city = '';
    this.address = '';
    this.normalizedAddress = '';
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
    this.rawOpeningHours = '';
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

  setNormalizedAddress(normalizedAddress) {
    if (normalizedAddress != null)
      this.normalizedAddress = sanitizeCSVField(normalizedAddress);
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

  setRawOpeningHours(rawOpeningHours) {
    if (rawOpeningHours != null)
      this.rawOpeningHours = sanitizeCSVField(rawOpeningHours);
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
    storeLocatorCsvEntity.setNormalizedAddress(
      registry.normalizedAddress.addressRow
    );
    storeLocatorCsvEntity.setCity(registry.normalizedAddress.city);
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

  if (registry.address) {
    storeLocatorCsvEntity.setAddress(
      `${registry.address.addressRow}, ${registry.address.cap} ${registry.address.city}`
    );
  }

  if (registry.phoneNumber) {
    storeLocatorCsvEntity.setPhoneNumber(
      registry.phoneNumber.replace(/\//g, ' ')
    );
  }

  /**
   * The property openingTime can be either an object or a string
   * An object is returned when openingTime is properly formatted
   * A string is returned when openingTime is not properly formatted,
   * so the raw string is saved instead
   */
  if (registry.openingTime) {
    if (
      typeof registry.openingTime === 'object' &&
      registry.openingTime !== null
    ) {
      const formattedOpeningTime = getOpeningTimeByDay(registry.openingTime);

      storeLocatorCsvEntity.setMonday(formattedOpeningTime[0]);
      storeLocatorCsvEntity.setTuesday(formattedOpeningTime[1]);
      storeLocatorCsvEntity.setWednesday(formattedOpeningTime[2]);
      storeLocatorCsvEntity.setThursday(formattedOpeningTime[3]);
      storeLocatorCsvEntity.setFriday(formattedOpeningTime[4]);
      storeLocatorCsvEntity.setSaturday(formattedOpeningTime[5]);
      storeLocatorCsvEntity.setSunday(formattedOpeningTime[6]);
    } else if (typeof registry.openingTime === 'string') {
      storeLocatorCsvEntity.setRawOpeningHours(registry.openingTime);
    }
  }

  return storeLocatorCsvEntity;
};

module.exports = { mapApiResponseToStoreLocatorCsvEntities, sanitizeCSVField };
