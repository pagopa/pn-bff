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

describe('StoreLocatorCsvEntity', () => {
  let placesClientMock;

  beforeEach(() => {
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
      type: 'CAF',
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
    expect(result.longitude).to.equal('9.1876');
    expect(result.latitude).to.equal('45.4669');
    expect(result.type).to.equal('CAF');
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
    expect(result.longitude).to.equal('9.1876');
    expect(result.latitude).to.equal('45.4669');
    expect(result.type).to.equal('CAF');
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
    expect(result.longitude).to.equal('9.1876');
    expect(result.latitude).to.equal('45.4669');
    expect(result.type).to.equal('CAF');
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
    expect(result.type).to.equal('CAF');
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
    expect(result.type).to.equal('CAF');
  });
});
