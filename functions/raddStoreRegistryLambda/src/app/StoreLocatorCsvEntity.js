const { sanitizeCSVField, getOpeningTimeByDay } = require('./csvUtils');

class StoreLocatorCsvEntity {
  constructor() {
    this.locationId = '';
    this.partnerId = '';
    this.description = '';
    this.city = '';
    this.address = '';
    this.normalizedAddress = '';
    this.province = '';
    this.zipCode = '';
    this.phoneNumbers = '';
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
    this.email = '';
    this.website = '';
    this.appointmentRequired = false;
  }

  setLocationId(locationId) {
    if (locationId != null) this.locationId = sanitizeCSVField(locationId);
  }

  setPartnerId(partnerId) {
    if (partnerId != null) this.partnerId = sanitizeCSVField(partnerId);
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

  setPhoneNumbers(phoneNumbers) {
    if (phoneNumbers != null)
      this.phoneNumbers = sanitizeCSVField(phoneNumbers);
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

  setEmail(email) {
    if (email != null) this.email = sanitizeCSVField(email);
  }

  setWebsite(website) {
    if (website != null) this.website = sanitizeCSVField(website);
  }

  setAppointmentRequired(appointmentRequired) {
    this.appointmentRequired = Boolean(appointmentRequired);
  }
}

/**
 * Returns a StoreLocatorCsvEntity instance if the awsScore exceeds the malformedAddressThreshold
 * Otherwise, the address will be skipped
 *
 * @param registry - The raw registry to be mapped into a StoreLocatorCsvEntity
 */
const mapApiResponseToStoreLocatorCsvEntities = (registry) => {
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

  if (registry.locationId) {
    storeLocatorCsvEntity.setLocationId(registry.locationId);
  }

  if (registry.partnerId) {
    storeLocatorCsvEntity.setPartnerId(registry.partnerId);
  }

  storeLocatorCsvEntity.setDescription(registry.description);
  if (registry.normalizedAddress) {
    storeLocatorCsvEntity.setNormalizedAddress(
      registry.normalizedAddress.addressRow
    );
    storeLocatorCsvEntity.setCity(registry.normalizedAddress.city);
    storeLocatorCsvEntity.setProvince(registry.normalizedAddress.province);
    storeLocatorCsvEntity.setZipCode(registry.normalizedAddress.cap);
    storeLocatorCsvEntity.setLatitude(registry.normalizedAddress.latitude);
    storeLocatorCsvEntity.setLongitude(registry.normalizedAddress.longitude);
  }

  if (registry.address) {
    storeLocatorCsvEntity.setAddress(
      `${registry.address.addressRow}, ${registry.address.cap} ${registry.address.city}`
    );
  }

  if (registry.phoneNumbers) {
    storeLocatorCsvEntity.setPhoneNumbers(
      registry.phoneNumbers.join('_').replace(/\//g, ' ')
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

  if (registry.email) {
    storeLocatorCsvEntity.setEmail(registry.email);
  }

  if (registry.website) {
    storeLocatorCsvEntity.setWebsite(registry.website);
  }

  if (registry.appointmentRequired !== undefined) {
    storeLocatorCsvEntity.setAppointmentRequired(registry.appointmentRequired);
  }

  return storeLocatorCsvEntity;
};

module.exports = {
  mapApiResponseToStoreLocatorCsvEntities,
  sanitizeCSVField,
};
