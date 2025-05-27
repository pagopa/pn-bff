const chai = require('chai');
const chaiAsPromised = require('chai-as-promised');
const { mockClient } = require('aws-sdk-client-mock');
const {
  GeoPlacesClient,
  GeocodeCommand,
} = require('@aws-sdk/client-geo-places');
const { getCoordinatesForAddress } = require('../app/geocodeUtils');

chai.use(chaiAsPromised);
const expect = chai.expect;

describe('geocodeUtils tests', function () {
  let placesClientMock;

  beforeEach(() => {
    placesClientMock = mockClient(GeoPlacesClient);
  });

  afterEach(() => {
    placesClientMock.reset();
  });

  it('should return coordinates when a valid address is provided', async () => {
    const mockResponse = {
      ResultItems: [
        {
          Title: 'Via Roma 123, Milano (MI), 20100',
          Position: [9.1876, 45.4669],
          MatchScores: {
            Overall: 0.96,
          },
          Address: {
            Region: {
              Name: 'Lombardia',
            },
          },
        },
      ],
    };

    placesClientMock.on(GeocodeCommand).resolves(mockResponse);

    const result = await getCoordinatesForAddress(
      'Via Roma 123',
      'MI',
      '20100',
      'Milano'
    );

    expect(result).to.deep.equal({
      awsLongitude: 9.1876,
      awsLatitude: 45.4669,
      awsAddress: 'Via Roma 123, Milano (MI), 20100',
      awsAddressRegion: 'Lombardia',
      awsScore: 0.96,
    });

    const commandCalls = placesClientMock.commandCalls(GeocodeCommand);
    expect(commandCalls.length).to.equal(1);
    expect(commandCalls[0].args[0].input).to.deep.include({
      MaxResults: 1,
      Filter: {
        IncludeCountries: ['IT'],
      },
      QueryComponents: {
        SubRegion: 'MI',
        PostalCode: '20100',
        Locality: 'Milano',
        Street: 'Via Roma 123',
      },
      Language: 'it',
    });
  });

  it('should return null when no results are found', async () => {
    const mockResponse = {
      ResultItems: [],
    };

    placesClientMock.on(GeocodeCommand).resolves(mockResponse);

    const result = await getCoordinatesForAddress(
      'Via Insesistente',
      'RM',
      '00100',
      'Roma'
    );

    expect(result).to.be.null;
  });

  it('should return null when coordinates are missing in the response', async () => {
    const mockResponse = {
      ResultItems: [
        {
          Title: 'Via Pippo, Roma, RM 00100, Italy',
          Position: [],
          MatchScores: {
            Overall: 0.5,
          },
          Address: {
            Region: {
              Name: 'Lazio',
            },
          },
        },
      ],
    };

    placesClientMock.on(GeocodeCommand).resolves(mockResponse);

    const result = await getCoordinatesForAddress(
      'Via Pippo',
      'RM',
      '00100',
      'Roma'
    );

    expect(result).to.deep.equal({
      awsLongitude: null,
      awsLatitude: null,
      awsAddress: 'Via Pippo, Roma, RM 00100, Italy',
      awsAddressRegion: 'Lazio',
      awsScore: 0.5,
    });
  });

  it('should handle missing MatchScores.Overall', async () => {
    const mockResponse = {
      ResultItems: [
        {
          Title: 'Via Nazionale 45, Roma, RM 00187, Italy',
          Position: [12.4845, 41.9056],
          MatchScores: undefined,
        },
      ],
    };

    placesClientMock.on(GeocodeCommand).resolves(mockResponse);

    const result = await getCoordinatesForAddress(
      'Via Nazionale 45',
      'RM',
      '00187',
      'Roma'
    );

    expect(result).to.deep.equal({
      awsLongitude: 12.4845,
      awsLatitude: 41.9056,
      awsAddress: 'Via Nazionale 45, Roma, RM 00187, Italy',
      awsAddressRegion: '',
      awsScore: 0,
    });
  });

  it('should throw an error when the API call fails', async () => {
    const errorMessage = 'Error during API fetch';
    placesClientMock.on(GeocodeCommand).rejects(new Error(errorMessage));

    try {
      await getCoordinatesForAddress('Via Roma 123', 'MI', '20100', 'Milano');

      expect.fail('Error during API fetch');
    } catch (error) {
      expect(error).to.be.an('Error');
      expect(error.message).to.equal(errorMessage);
    }
  });
});
