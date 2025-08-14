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

  it('should map API response correctly', () => {
    const registry = raddAltApiResponse[0];

    const result = mapApiResponseToStoreLocatorCsvEntities(registry);

    expect(result.locationId).to.equal('"LOC-54321"');
    expect(result.partnerId).to.equal('"11223344556"');
    expect(result.description).to.equal('"CAF Milano"');
    expect(result.city).to.equal('"Milano"');
    expect(result.normalizedAddress).to.equal('"Piazza del Duomo 1"');
    expect(result.address).to.equal('"VIA PIAZZA DEL DUOMO 1, 20121 MILANO"');
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
    expect(result.appointmentRequired).to.be.false;
  });

  it('should handle null values correctly', () => {
    const registry = {
      locationId: null,
      partnerId: null,
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
      appointmentRequired: undefined,
    };

    const result = mapApiResponseToStoreLocatorCsvEntities(registry);
    expect(result.locationId).to.equal('');
    expect(result.partnerId).to.equal('');
    expect(result.description).to.equal('');
    expect(result.city).to.equal('');
    expect(result.normalizedAddress).to.equal('');
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
    expect(result.appointmentRequired).to.be.false;
  });

  it('should skip address when AWS score is below MALFORMED_ADDRESS_THRESHOLD', () => {
    const registry = {
      ...raddAltApiResponse[0],
      normalizedAddress: {
        ...raddAltApiResponse[0].normalizedAddress,
        biasPoint: {
          overall: 0.2,
        },
      },
    };

    const result = mapApiResponseToStoreLocatorCsvEntities(registry);

    expect(result).to.be.undefined;
  });

  it('should skip address when biasPoint is not available', () => {
    const registry = {
      ...raddAltApiResponse[0],
      normalizedAddress: {
        ...raddAltApiResponse[0].normalizedAddress,
        biasPoint: null,
      },
    };

    const result = mapApiResponseToStoreLocatorCsvEntities(registry);

    expect(result).to.be.undefined;
  });

  it('should handle malformed opening hours', () => {
    const registry = {
      ...raddAltApiResponse[0],
      openingTime: 'Lunedi dalle 10 alle 12:00 ; 14:00',
    };

    const result = mapApiResponseToStoreLocatorCsvEntities(registry);

    expect(result.description).to.equal('"CAF Milano"');
    expect(result.city).to.equal('"Milano"');
    expect(result.normalizedAddress).to.equal('"Piazza del Duomo 1"');
    expect(result.address).to.equal('"VIA PIAZZA DEL DUOMO 1, 20121 MILANO"');
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

    const result = mapApiResponseToStoreLocatorCsvEntities(registry);

    expect(result.monday).to.equal('');
  });

  it('should handle multiple phone numbers', () => {
    const registry = {
      ...raddAltApiResponse[0],
      phoneNumbers: ['0212345678', '0298765432'],
    };

    const result = mapApiResponseToStoreLocatorCsvEntities(registry);

    expect(result.phoneNumbers).to.equal('"0212345678_0298765432"');
  });

  it('should handle phone numbers with forward slashes', () => {
    const registry = {
      ...raddAltApiResponse[0],
      phoneNumbers: ['021/2345678', '029/8765432'],
    };

    const result = mapApiResponseToStoreLocatorCsvEntities(registry);
    expect(result.phoneNumbers).to.equal('"021 2345678_029 8765432"');
  });

  it('should handle empty phone numbers array', () => {
    const registry = {
      ...raddAltApiResponse[0],
      phoneNumbers: [],
    };

    const result = mapApiResponseToStoreLocatorCsvEntities(registry);
    expect(result.phoneNumbers).to.equal('');
  });
});
