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
        },
      ],
    };

    placesClientMock.on(GeocodeCommand).resolves(mockResponse);

    const result = await getCoordinatesForAddress(
      'Via Roma 123, Milano, MI 20100',
      'MI',
      '20100',
      'Milano'
    );

    expect(result).to.deep.equal({
      longitude: 9.1876,
      latitude: 45.4669,
      address: 'Via Roma 123, Milano (MI), 20100',
      score: 0.96,
    });

    const commandCalls = placesClientMock.commandCalls(GeocodeCommand);
    expect(commandCalls.length).to.equal(1);
    expect(commandCalls[0].args[0].input).to.deep.include({
      QueryText: 'Via Roma 123, Milano, MI 20100',
      MaxResults: 1,
      Language: 'it',
      QueryComponents: {
        Country: 'IT',
        SubRegion: 'MI',
        PostalCode: '20100',
        Locality: 'Milano',
      },
    });
  });

  it('should return null when no results are found', async () => {
    const mockResponse = {
      ResultItems: [],
    };

    placesClientMock.on(GeocodeCommand).resolves(mockResponse);

    const result = await getCoordinatesForAddress(
      'Via Insesistente, ROMA (RM), 00100',
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
        },
      ],
    };

    placesClientMock.on(GeocodeCommand).resolves(mockResponse);

    const result = await getCoordinatesForAddress(
      'Via Pippo, ROMA (RM), 00100',
      'RM',
      '00100',
      'Roma'
    );

    expect(result).to.deep.equal({
      longitude: null,
      latitude: null,
      address: 'Via Pippo, Roma, RM 00100, Italy',
      score: 0.5,
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
      'Via Nazionale 45 Roma 00187',
      'RM',
      '00187',
      'Roma'
    );

    expect(result).to.deep.equal({
      longitude: 12.4845,
      latitude: 41.9056,
      address: 'Via Nazionale 45, Roma, RM 00187, Italy',
      score: 0,
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
