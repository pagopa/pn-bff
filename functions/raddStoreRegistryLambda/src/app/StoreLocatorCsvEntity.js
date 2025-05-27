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
    this.latitude = '';
    this.longitude = '';
  }

  setDescription(description) {
    if (description != null) this.description = description;
  }

  setCity(city) {
    if (city != null) this.city = city;
  }

  setAddress(address) {
    if (address != null) this.address = address;
  }

  setAwsAddress(awsAddress) {
    if (awsAddress != null) this.awsAddress = awsAddress;
  }

  setProvince(province) {
    if (province != null) this.province = province;
  }

  setRegion(region) {
    if (region != null) this.region = region;
  }

  setZipCode(zipCode) {
    if (zipCode != null) this.zipCode = zipCode;
  }

  setPhoneNumber(phoneNumber) {
    if (phoneNumber != null) this.phoneNumber = phoneNumber;
  }

  setMonday(monday) {
    if (monday != null) this.monday = monday;
  }

  setTuesday(tuesday) {
    if (tuesday != null) this.tuesday = tuesday;
  }

  setWednesday(wednesday) {
    if (wednesday != null) this.wednesday = wednesday;
  }

  setThursday(thursday) {
    if (thursday != null) this.thursday = thursday;
  }

  setFriday(friday) {
    if (friday != null) this.friday = friday;
  }

  setSaturday(saturday) {
    if (saturday != null) this.saturday = saturday;
  }

  setSunday(sunday) {
    if (sunday != null) this.sunday = sunday;
  }

  setLatitude(latitude) {
    if (latitude != null) this.latitude = latitude.toString();
  }

  setLongitude(longitude) {
    if (longitude != null) this.longitude = longitude.toString();
  }
}

const mapApiResponseToStoreLocatorCsvEntities = async (registry) => {
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

    if (coordinatesResponse) {
      storeLocatorCsvEntity.setLatitude(coordinatesResponse.awsLatitude);
      storeLocatorCsvEntity.setLongitude(coordinatesResponse.awsLongitude);
      storeLocatorCsvEntity.setAwsAddress(coordinatesResponse.awsAddress);
      storeLocatorCsvEntity.setRegion(coordinatesResponse.awsAddressRegion);
    }
  } catch (e) {
    console.log(e);
  }

  return storeLocatorCsvEntity;
};

module.exports = { mapApiResponseToStoreLocatorCsvEntities };
