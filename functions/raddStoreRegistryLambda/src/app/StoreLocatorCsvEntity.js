class StoreLocatorCsvEntity {
    constructor() {
        this.description = "";
        this.city = "";
        this.address = "";
        this.province = "";
        this.zipCode = "";
        this.phoneNumber = "";
        this.monday = "";
        this.tuesday = "";
        this.wednesday = "";
        this.thursday = "";
        this.friday = "";
        this.saturday = "";
        this.sunday = "";
        this.latitude = "";
        this.longitude = "";
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

    setProvince(province) {
        if (province != null) this.province = province;
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
        if (latitude != null) this.latitude = latitude;
    }

    setLongitude(longitude) {
        if (longitude != null) this.longitude = longitude;
    }
}

const mapApiResponseToStoreLocatorCsvEntities = (registry) => {
    console.log('Mapping API response to StoreLocatorCsvEntity:', registry);

    const getOpeningTimeByDay = (fullOpeningTime) => {
        console.log('Parsing opening time:', fullOpeningTime);
        let times = new Array(7).fill(null);
        if (fullOpeningTime) {
            let days = fullOpeningTime.split("#");
            for (let day of days) {
                switch (day.substring(0, 3).toUpperCase()) {
                    case "MON":
                        times[0] = day.substring(4);
                        break;
                    case "TUE":
                        times[1] = day.substring(4);
                        break;
                    case "WED":
                        times[2] = day.substring(4);
                        break;
                    case "THU":
                        times[3] = day.substring(4);
                        break;
                    case "FRI":
                        times[4] = day.substring(4);
                        break;
                    case "SAT":
                        times[5] = day.substring(4);
                        break;
                    case "SUN":
                        times[6] = day.substring(4);
                        break;
                }
            }
        }
        console.log('Parsed times:', times);
        return times;
    }

    const storeLocatorCsvEntity = new StoreLocatorCsvEntity();

    storeLocatorCsvEntity.setDescription(registry.description);
    console.log('Set description:', registry.description);
    if (registry.address) {
        storeLocatorCsvEntity.setCity(registry.address.city);
        storeLocatorCsvEntity.setAddress(registry.address.addressRow);
        storeLocatorCsvEntity.setProvince(registry.address.pr);
        storeLocatorCsvEntity.setZipCode(registry.address.cap);
        console.log('Set address:', registry.address);
    }
    storeLocatorCsvEntity.setPhoneNumber(registry.phoneNumber.replace(/\//g, ' '));
    console.log('Set phone number:', registry.phoneNumber);
        
    if (registry.openingTime) {
        const formattedOpeningTime = getOpeningTimeByDay(registry.openingTime);
        storeLocatorCsvEntity.setMonday(formattedOpeningTime[0]);
        storeLocatorCsvEntity.setTuesday(formattedOpeningTime[1]);
        storeLocatorCsvEntity.setWednesday(formattedOpeningTime[2]);
        storeLocatorCsvEntity.setThursday(formattedOpeningTime[3]);
        storeLocatorCsvEntity.setFriday(formattedOpeningTime[4]);
        storeLocatorCsvEntity.setSaturday(formattedOpeningTime[5]);
        storeLocatorCsvEntity.setSunday(formattedOpeningTime[6]);
        console.log('Set opening times:', formattedOpeningTime);
    }

    if (registry.geoLocation) {
        storeLocatorCsvEntity.setLatitude(registry.geoLocation.latitude);
        storeLocatorCsvEntity.setLongitude(registry.geoLocation.longitude);
        console.log('Set geolocation:', registry.geoLocation);
    }

    console.log('Mapped entity:', storeLocatorCsvEntity);
    return storeLocatorCsvEntity;
}

module.exports = { mapApiResponseToStoreLocatorCsvEntities };
