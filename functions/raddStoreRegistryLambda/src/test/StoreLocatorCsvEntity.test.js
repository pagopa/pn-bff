const { expect } = require('chai');
const sinon = require('sinon');
const {
  mapApiResponseToStoreLocatorCsvEntities,
  sanitizeCSVField,
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

  it('should map API response correctly', async () => {
    const registry = raddAltApiResponse[0];

    const result = await mapApiResponseToStoreLocatorCsvEntities(registry);

    expect(result.description).to.equal('"CAF Milano"');
    expect(result.city).to.equal('"Milano"');
    expect(result.address).to.equal('"Piazza del Duomo 1"');
    expect(result.province).to.equal('"MI"');
    expect(result.zipCode).to.equal('"20121"');
    expect(result.longitude).to.equal('"9.1900"');
    expect(result.latitude).to.equal('"45.4642"');
    // expect(result.phoneNumber).to.equal('"123 456 7890"');
    expect(result.monday).to.equal('');
    expect(result.tuesday).to.equal('');
    expect(result.wednesday).to.equal('');
    expect(result.thursday).to.equal('');
    expect(result.friday).to.equal('');
    expect(result.saturday).to.equal('');
    expect(result.sunday).to.equal('');
    expect(result.cafOpeningHours).to.equal(
      '"Lun 09:00-17:00; Gio 09:00-12:00"'
    );
  });

  // TODO - Will be modified with https://pagopa.atlassian.net/browse/PN-15548
  // it('should map API response correctly when there is only one day in openingTime', async () => {
  //   const registry = {
  //     description: 'Test Store',
  //     address: {
  //       city: 'Test City',
  //       addressRow: '123 Test St',
  //       pr: 'Test Province',
  //       cap: '12345',
  //     },
  //     phoneNumber: '123/456/7890',
  //     openingTime: 'MON 09:00-17:00#',
  //   };

  //   mockGeoPlacesResponse(9.1876, 45.4669, 1);
  //   const { storeRecord: result, malformedRecord } =
  //     await mapApiResponseToStoreLocatorCsvEntities(registry);

  //   expect(result.description).to.equal('"Test Store"');
  //   expect(result.city).to.equal('"Test City"');
  //   expect(result.address).to.equal('"123 Test St"');
  //   expect(result.province).to.equal('"Test Province"');
  //   expect(result.zipCode).to.equal('"12345"');
  //   expect(result.phoneNumber).to.equal('"123 456 7890"');
  //   expect(result.monday).to.equal('"09:00-17:00"');
  //   expect(result.tuesday).to.equal('');
  //   expect(result.wednesday).to.equal('');
  //   expect(result.thursday).to.equal('');
  //   expect(result.friday).to.equal('');
  //   expect(result.saturday).to.equal('');
  //   expect(result.sunday).to.equal('');
  //   expect(result.cafOpeningHours).to.equal('');
  //   expect(result.longitude).to.equal('"9.1876"');
  //   expect(result.latitude).to.equal('"45.4669"');
  //   expect(result.awsAddress).to.equal('"Via Roma 123, Milano (MI), 20100"');
  //   expect(result.region).to.equal('"Lombardia"');
  //   expect(malformedRecord).to.be.null;
  // });

  it('should handle null values correctly', async () => {
    const registry = {
      description: null,
      normalizedAddress: null,
      phoneNumber: null,
      openingTime: null,
    };

    const result = await mapApiResponseToStoreLocatorCsvEntities(registry);
    expect(result.description).to.equal('');
    expect(result.city).to.equal('');
    expect(result.address).to.equal('');
    expect(result.province).to.equal('');
    expect(result.zipCode).to.equal('');
    expect(result.phoneNumber).to.equal('');
    expect(result.monday).to.equal('');
    expect(result.tuesday).to.equal('');
    expect(result.wednesday).to.equal('');
    expect(result.thursday).to.equal('');
    expect(result.friday).to.equal('');
    expect(result.saturday).to.equal('');
    expect(result.sunday).to.equal('');
    expect(result.latitude).to.equal('');
    expect(result.longitude).to.equal('');
  });

  // TODO - Will be modified with https://pagopa.atlassian.net/browse/PN-15547
  it('should add address to wrongAddressesArray when AWS score is below 0.7', async () => {
    const registry = {
      ...raddAltApiResponse[0],
      normalizedAddress: {
        ...raddAltApiResponse[0].normalizedAddress,
        biasPoint: {
          overall: 0.2,
        },
      },
    };

    const result = await mapApiResponseToStoreLocatorCsvEntities(registry);

    // Here, with PN-15547, we only need to check that result is null

    expect(result.longitude).to.equal('');
    expect(result.latitude).to.equal('');
  });

  it('should handle malformed opening hours', async () => {
    const registry = {
      ...raddAltApiResponse[0],
      openingTime: 'Lunedi dalle 10 alle 12:00 ; 14:00',
    };

    const result = await mapApiResponseToStoreLocatorCsvEntities(registry);

    expect(result.description).to.equal('"CAF Milano"');
    expect(result.city).to.equal('"Milano"');
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
    expect(result.cafOpeningHours).to.equal(
      '"Lunedi dalle 10 alle 12:00 ; 14:00"'
    );
  });
});

describe('sanitizeCSVField', () => {
  it('should return empty string for null or undefined', () => {
    expect(sanitizeCSVField(null)).to.equal('');
    expect(sanitizeCSVField(undefined)).to.equal('');
  });

  it('should return empty string for empty string', () => {
    expect(sanitizeCSVField('')).to.equal('');
  });

  it('should return empty string for whitespace-only strings', () => {
    expect(sanitizeCSVField('   ')).to.equal('');
  });

  it('should wrap simple strings in quotes', () => {
    expect(sanitizeCSVField('hello')).to.equal('"hello"');
    expect(sanitizeCSVField('Test Store')).to.equal('"Test Store"');
    expect(sanitizeCSVField('123')).to.equal('"123"');
  });

  it('should trim whitespace and wrap in quotes', () => {
    expect(sanitizeCSVField('  hello  ')).to.equal('"hello"');
    expect(sanitizeCSVField('\t  Test Store  \n')).to.equal('"Test Store"');
  });

  it('should handle semicolons (CSV delimiters)', () => {
    expect(sanitizeCSVField('Mon-Fri 9:00-17:00; Sat 10:00-14:00')).to.equal(
      '"Mon-Fri 9:00-17:00; Sat 10:00-14:00"'
    );
  });

  it('should replace newlines with spaces', () => {
    expect(sanitizeCSVField('Line 1\nLine 2')).to.equal('"Line 1 Line 2"');
  });

  it('should replace tabs with spaces', () => {
    expect(sanitizeCSVField('Tab\tSeparated\tValues')).to.equal(
      '"Tab Separated Values"'
    );
  });
});
