const { expect } = require('chai');
const sinon = require('sinon');
const {
  mapApiResponseToStoreLocatorCsvEntities,
} = require('../app/StoreLocatorCsvEntity');
const { mockClient } = require('aws-sdk-client-mock');
const {
  GeoPlacesClient,
  GeocodeCommand,
} = require('@aws-sdk/client-geo-places');
const { setupEnv } = require('./utils/test.utils');

describe('StoreLocatorCsvEntity', () => {
  let placesClientMock;

  beforeEach(() => {
    setupEnv();
    placesClientMock = mockClient(GeoPlacesClient);
  });

  afterEach(() => {
    sinon.restore();
    placesClientMock.reset();
  });

  const mockGeoPlacesResponse = (longitude, latitude, score) => {
    placesClientMock.on(GeocodeCommand).resolves({
      ResultItems: [
        {
          Title: 'Via Roma 123, Milano (MI), 20100',
          Position: [longitude, latitude],
          MatchScores: {
            Overall: score,
          },
          Address: {
            Region: {
              Name: 'Lombardia',
            },
          },
        },
      ],
    });
  };

  const mockGeoPlacesErrorResponse = () => {
    placesClientMock.on(GeocodeCommand).rejects(new Error());
  };

  it('should map API response correctly', async () => {
    const registry = {
      description: 'Test Store',
      address: {
        city: 'Test City',
        addressRow: '123 Test St',
        pr: 'Test Province',
        cap: '12345',
      },
      phoneNumber: '123/456/7890',
      openingTime:
        'MON 09:00-17:00#TUE 09:00-17:00#WED 09:00-17:00#THU 09:00-17:00#FRI 09:00-17:00#SAT 10:00-14:00#SUN closed',
    };

    mockGeoPlacesResponse(9.1876, 45.4669, 1);
    const result = await mapApiResponseToStoreLocatorCsvEntities(registry);

    expect(result.description).to.equal('Test Store');
    expect(result.city).to.equal('Test City');
    expect(result.address).to.equal('123 Test St');
    expect(result.province).to.equal('Test Province');
    expect(result.zipCode).to.equal('12345');
    expect(result.phoneNumber).to.equal('123 456 7890');
    expect(result.monday).to.equal('09:00-17:00');
    expect(result.tuesday).to.equal('09:00-17:00');
    expect(result.wednesday).to.equal('09:00-17:00');
    expect(result.thursday).to.equal('09:00-17:00');
    expect(result.friday).to.equal('09:00-17:00');
    expect(result.saturday).to.equal('10:00-14:00');
    expect(result.sunday).to.equal('closed');
    expect(result.longitude).to.equal(9.1876);
    expect(result.latitude).to.equal(45.4669);
    expect(result.awsAddress).to.equal('Via Roma 123, Milano (MI), 20100');
    expect(result.region).to.equal('Lombardia');
  });

  it('should map API response correctly when there is only one day in openingTime', async () => {
    const registry = {
      description: 'Test Store',
      address: {
        city: 'Test City',
        addressRow: '123 Test St',
        pr: 'Test Province',
        cap: '12345',
      },
      phoneNumber: '123/456/7890',
      openingTime: 'MON 09:00-17:00#',
    };

    mockGeoPlacesResponse(9.1876, 45.4669, 1);
    const result = await mapApiResponseToStoreLocatorCsvEntities(registry);

    expect(result.description).to.equal('Test Store');
    expect(result.city).to.equal('Test City');
    expect(result.address).to.equal('123 Test St');
    expect(result.province).to.equal('Test Province');
    expect(result.zipCode).to.equal('12345');
    expect(result.phoneNumber).to.equal('123 456 7890');
    expect(result.monday).to.equal('09:00-17:00');
    expect(result.tuesday).to.equal('');
    expect(result.wednesday).to.equal('');
    expect(result.thursday).to.equal('');
    expect(result.friday).to.equal('');
    expect(result.saturday).to.equal('');
    expect(result.sunday).to.equal('');
    expect(result.longitude).to.equal(9.1876);
    expect(result.latitude).to.equal(45.4669);
    expect(result.awsAddress).to.equal('Via Roma 123, Milano (MI), 20100');
    expect(result.region).to.equal('Lombardia');
  });

  it('should handle missing optional fields gracefully', async () => {
    const registry = {
      description: 'Test Store',
      address: {
        city: 'Test City',
        addressRow: '123 Test St',
        pr: 'Test Province',
        cap: '12345',
      },
      phoneNumber: '123/456/7890',
    };

    mockGeoPlacesResponse(9.1876, 45.4669, 1);
    const result = await mapApiResponseToStoreLocatorCsvEntities(registry);

    expect(result.description).to.equal('Test Store');
    expect(result.city).to.equal('Test City');
    expect(result.address).to.equal('123 Test St');
    expect(result.province).to.equal('Test Province');
    expect(result.zipCode).to.equal('12345');
    expect(result.phoneNumber).to.equal('123 456 7890');
    expect(result.monday).to.equal('');
    expect(result.tuesday).to.equal('');
    expect(result.wednesday).to.equal('');
    expect(result.thursday).to.equal('');
    expect(result.friday).to.equal('');
    expect(result.saturday).to.equal('');
    expect(result.sunday).to.equal('');
    expect(result.longitude).to.equal(9.1876);
    expect(result.latitude).to.equal(45.4669);
    expect(result.awsAddress).to.equal('Via Roma 123, Milano (MI), 20100');
    expect(result.region).to.equal('Lombardia');
  });

  it('should handle null values correctly', async () => {
    const registry = {
      description: null,
      address: null,
      phoneNumber: null,
      openingTime: null,
      geoLocation: null,
    };

    mockGeoPlacesResponse(null, null, 1);
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
    expect(result.awsAddress).to.equal('');
    expect(result.region).to.equal('');
  });

  it('should handle error in geolocate function', async () => {
    const registry = {
      description: 'Test Store',
      address: {
        city: 'Test City',
        addressRow: '123 Test St',
        pr: 'Test Province',
        cap: '12345',
      },
      phoneNumber: '123/456/7890',
    };

    mockGeoPlacesErrorResponse();
    const result = await mapApiResponseToStoreLocatorCsvEntities(registry);

    expect(result.description).to.equal('Test Store');
    expect(result.city).to.equal('Test City');
    expect(result.address).to.equal('123 Test St');
    expect(result.province).to.equal('Test Province');
    expect(result.zipCode).to.equal('12345');
    expect(result.phoneNumber).to.equal('123 456 7890');
    expect(result.longitude).to.equal('');
    expect(result.latitude).to.equal('');
    expect(result.awsAddress).to.equal('');
    expect(result.region).to.equal('');
  });

  it('should add address to wrongAddressesArray when AWS score is below 0.8', async () => {
    const registry = {
      description: 'CAF UIL',
      address: {
        city: 'Milano',
        addressRow: 'Via Carlo Magno 1',
        pr: 'MI',
        cap: '20100',
      },
    };

    mockGeoPlacesResponse(9.19, 45.4642, 0.7);

    const wrongAddressesArray = [];
    await mapApiResponseToStoreLocatorCsvEntities(
      registry,
      wrongAddressesArray
    );

    expect(wrongAddressesArray.length).to.equal(1);

    const row = wrongAddressesArray[0];
    expect(row).to.include('CAF UIL');
    expect(row).to.include('Via Carlo Magno 1');
    expect(row).to.include('Milano');
    expect(row).to.include('MI');
    expect(row).to.include('Via Roma 123, Milano (MI), 20100');
    expect(row).to.include('0.7');
    expect(row).to.include('45.4642');
    expect(row).to.include('9.19');
  });

  it('should not add address to wrongAddressesArray when AWS score is above malformed address threshold', async () => {
    const registry = {
      description: 'CAF CGIL',
      address: {
        city: 'Roma',
        addressRow: 'Via Nazionale 42',
        pr: 'RM',
        cap: '00100',
      },
    };

    mockGeoPlacesResponse(12.4964, 41.9028, 0.9);

    const wrongAddressesArray = [];
    await mapApiResponseToStoreLocatorCsvEntities(
      registry,
      wrongAddressesArray
    );

    expect(wrongAddressesArray.length).to.equal(0);
  });
});
