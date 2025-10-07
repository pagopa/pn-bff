const { expect } = require('chai');
const sinon = require('sinon');
const {
  mapApiResponseToStoreLocatorCsvEntities,
} = require('../app/StoreLocatorCsvEntity');
const { setupEnv } = require('./utils/test.utils');
const { raddAltApiResponse } = require('../__mocks__/registries.mock');

describe('StoreLocatorCsvEntity', () => {
  beforeEach(() => {
    setupEnv();
  });

  afterEach(() => {
    sinon.restore();
  });

  it('should map API response correctly with a valid AWS address', () => {
    const registry = raddAltApiResponse[0];

    const { record: result, isRecordValid } =
      mapApiResponseToStoreLocatorCsvEntities(registry);

    expect(result.locationId).to.equal('"LOC-54321"');
    expect(result.partnerId).to.equal('"11223344556"');
    expect(result.externalCodes).to.equal('"MI-101 , MI-102"');
    expect(result.description).to.equal('"CAF Milano"');
    expect(result.city).to.equal('"Milano"');
    expect(result.normalizedAddress).to.equal('"Piazza del Duomo 1"');
    expect(result.cafAddress).to.equal(
      '"VIA PIAZZA DEL DUOMO 1, 20121 MILANO"'
    );
    expect(result.address).to.equal('"Piazza del Duomo 1"');
    expect(result.province).to.equal('"MI"');
    expect(result.zipCode).to.equal('"20121"');
    expect(result.longitude).to.equal('"9.1900"');
    expect(result.latitude).to.equal('"45.4642"');
    expect(result.phoneNumbers).to.equal('"0212345678"');
    expect(result.monday).to.equal('"09:00-13:00, 14:00-18:00"');
    expect(result.tuesday).to.equal('"09:00-13:00"');
    expect(result.wednesday).to.equal('"09:00-13:00, 14:00-18:00"');
    expect(result.thursday).to.equal('"09:00-13:00"');
    expect(result.friday).to.equal('"09:00-13:00, 14:00-18:00"');
    expect(result.saturday).to.equal('"09:00-12:00"');
    expect(result.sunday).to.equal('');
    expect(result.rawOpeningHours).to.equal('');
    expect(result.email).to.equal('"milano@mail.it"');
    expect(result.website).to.equal('"https://www.mock-website.it"');
    expect(result.appointmentRequired).to.equal('"no"');
    expect(result.biasPoint).to.equal(
      JSON.stringify(registry.normalizedAddress.biasPoint)
    );
    expect(isRecordValid).to.be.true;
  });

  it('should handle null values correctly', () => {
    const registry = {
      locationId: null,
      partnerId: null,
      externalCodes: null,
      description: null,
      normalizedAddress: {
        addressRow: null,
        cap: null,
        city: null,
        province: null,
        country: null,
        latitude: null,
        longitude: null,
        biasPoint: {
          overall: 0.98,
        },
      },
      phoneNumbers: null,
      openingTime: null,
      address: null,
      email: null,
      website: null,
      appointmentRequired: null,
    };

    const { record: result, isRecordValid } =
      mapApiResponseToStoreLocatorCsvEntities(registry);

    expect(result.locationId).to.equal('');
    expect(result.partnerId).to.equal('');
    expect(result.externalCodes).to.equal('');
    expect(result.description).to.equal('');
    expect(result.city).to.equal('');
    expect(result.normalizedAddress).to.equal('');
    expect(result.cafAddress).to.equal('');
    expect(result.address).to.equal('');
    expect(result.province).to.equal('');
    expect(result.zipCode).to.equal('');
    expect(result.phoneNumbers).to.equal('');
    expect(result.monday).to.equal('');
    expect(result.tuesday).to.equal('');
    expect(result.wednesday).to.equal('');
    expect(result.thursday).to.equal('');
    expect(result.friday).to.equal('');
    expect(result.saturday).to.equal('');
    expect(result.sunday).to.equal('');
    expect(result.rawOpeningHours).to.equal('');
    expect(result.latitude).to.equal('');
    expect(result.longitude).to.equal('');
    expect(result.email).to.equal('');
    expect(result.website).to.equal('');
    expect(result.appointmentRequired).to.equal('');
    expect(isRecordValid).to.be.false;
  });

  it('should return isValidRecord false and use cafAddress when AWS score is low', () => {
    const registry = {
      ...raddAltApiResponse[0],
      normalizedAddress: {
        ...raddAltApiResponse[0].normalizedAddress,
        biasPoint: {
          overall: 0.2,
        },
      },
    };

    const { record: result, isRecordValid } =
      mapApiResponseToStoreLocatorCsvEntities(registry);
    expect(result.cafAddress).to.equal(
      '"VIA PIAZZA DEL DUOMO 1, 20121 MILANO"'
    );
    expect(result.normalizedAddress).to.equal('"Piazza del Duomo 1"');
    expect(result.address).to.equal('"VIA PIAZZA DEL DUOMO 1, 20121 MILANO"');
    expect(isRecordValid).to.be.false;
  });

  it('should use cafAddress when biasPoint is not available', () => {
    const registry = {
      ...raddAltApiResponse[0],
      normalizedAddress: {
        ...raddAltApiResponse[0].normalizedAddress,
        biasPoint: null,
      },
    };

    const { record: result, isRecordValid } =
      mapApiResponseToStoreLocatorCsvEntities(registry);
    expect(result.cafAddress).to.equal(
      '"VIA PIAZZA DEL DUOMO 1, 20121 MILANO"'
    );
    expect(result.normalizedAddress).to.equal('"Piazza del Duomo 1"');
    expect(result.address).to.equal('"VIA PIAZZA DEL DUOMO 1, 20121 MILANO"');
    expect(result.biasPoint).to.equal('');
    expect(isRecordValid).to.be.false;
  });

  it('should handle malformed opening hours', () => {
    const registry = {
      ...raddAltApiResponse[0],
      openingTime: 'Lunedi dalle 10 alle 12:00 ; 14:00',
    };

    const { record: result } =
      mapApiResponseToStoreLocatorCsvEntities(registry);

    expect(result.description).to.equal('"CAF Milano"');
    expect(result.city).to.equal('"Milano"');
    expect(result.normalizedAddress).to.equal('"Piazza del Duomo 1"');
    expect(result.cafAddress).to.equal(
      '"VIA PIAZZA DEL DUOMO 1, 20121 MILANO"'
    );
    expect(result.address).to.equal('"Piazza del Duomo 1"');
    expect(result.province).to.equal('"MI"');
    expect(result.zipCode).to.equal('"20121"');
    expect(result.longitude).to.equal('"9.1900"');
    expect(result.latitude).to.equal('"45.4642"');
    expect(result.monday).to.equal('');
    expect(result.tuesday).to.equal('');
    expect(result.wednesday).to.equal('');
    expect(result.thursday).to.equal('');
    expect(result.friday).to.equal('');
    expect(result.saturday).to.equal('');
    expect(result.sunday).to.equal('');
    expect(result.rawOpeningHours).to.equal(
      '"Lunedi dalle 10 alle 12:00 ; 14:00"'
    );
  });

  it('should handle unkown key in opening hours', () => {
    const registry = {
      ...raddAltApiResponse[0],
      openingTime: {
        monday: '09:00-13:00, 14:00-18:00',
      },
    };

    const { record: result } =
      mapApiResponseToStoreLocatorCsvEntities(registry);

    expect(result.monday).to.equal('');
  });

  it('should handle empty opening hours object', () => {
    const registry = {
      ...raddAltApiResponse[0],
      openingTime: {},
    };

    const { record: result } =
      mapApiResponseToStoreLocatorCsvEntities(registry);

    expect(result.monday).to.equal('');
    expect(result.tuesday).to.equal('');
    expect(result.wednesday).to.equal('');
    expect(result.thursday).to.equal('');
    expect(result.friday).to.equal('');
    expect(result.saturday).to.equal('');
    expect(result.sunday).to.equal('');
    expect(result.rawOpeningHours).to.equal('');
  });

  it('should handle unknown type in opening hours', () => {
    const registry = {
      ...raddAltApiResponse[0],
      openingTime: 12345,
    };

    const { record: result } =
      mapApiResponseToStoreLocatorCsvEntities(registry);

    expect(result.monday).to.equal('');
    expect(result.tuesday).to.equal('');
    expect(result.wednesday).to.equal('');
    expect(result.thursday).to.equal('');
    expect(result.friday).to.equal('');
    expect(result.saturday).to.equal('');
    expect(result.sunday).to.equal('');
    expect(result.rawOpeningHours).to.equal('');
  });

  it('should handle multiple phone numbers', () => {
    const registry = {
      ...raddAltApiResponse[0],
      phoneNumbers: ['0212345678', '0298765432'],
    };

    const { record: result } =
      mapApiResponseToStoreLocatorCsvEntities(registry);

    expect(result.phoneNumbers).to.equal('"0212345678_0298765432"');
  });

  it('should handle phone numbers with forward slashes', () => {
    const registry = {
      ...raddAltApiResponse[0],
      phoneNumbers: ['021/2345678', '029/8765432'],
    };

    const { record: result } =
      mapApiResponseToStoreLocatorCsvEntities(registry);
    expect(result.phoneNumbers).to.equal('"021 2345678_029 8765432"');
  });

  it('should handle empty phone numbers array', () => {
    const registry = {
      ...raddAltApiResponse[0],
      phoneNumbers: [],
    };

    const { record: result } =
      mapApiResponseToStoreLocatorCsvEntities(registry);
    expect(result.phoneNumbers).to.equal('');
  });

  it('should handle missing appointmentRequired', () => {
    const registry = {
      ...raddAltApiResponse[0],
      appointmentRequired: undefined,
    };

    const { record: result } =
      mapApiResponseToStoreLocatorCsvEntities(registry);
    expect(result.appointmentRequired).to.equal('');
  });
});
