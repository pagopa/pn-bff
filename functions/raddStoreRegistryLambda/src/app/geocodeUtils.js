const {
  GeoPlacesClient,
  GeocodeCommand,
} = require('@aws-sdk/client-geo-places');

const client = new GeoPlacesClient({
  region: process.env.AWS_LOCATION_REGION,
});

/**
 * Get coordinates for a given address using AWS GeoPlaces API
 * @param address - The address to geocode
 * @param province - The province of the address (es. RM, MI, ...)
 * @param zip - The zip code of the address (es. 00100, 20100, ...)
 * @param municipality - The municipality of the address (es. Roma, Milano, ...)
 * @returns An object containing the latitude and longitude of the address
 */
const getCoordinatesForAddress = async (
  address,
  province,
  zip,
  municipality
) => {
  const params = {
    MaxResults: 1,
    Filter: {
      IncludeCountries: ['IT'],
    },
    QueryComponents: {
      SubRegion: province,
      PostalCode: zip,
      Locality: municipality,
      Street: address,
    },
    Language: 'it',
  };

  try {
    const command = new GeocodeCommand(params);
    const response = await client.send(command);

    if (response.ResultItems && response.ResultItems.length > 0) {
      const coordinates = response.ResultItems[0].Position;
      const score = response.ResultItems[0].MatchScores?.Overall || 0;

      if (!coordinates || coordinates.length < 2) {
        console.error(
          `No coordinates found in the response for address: ${address}`
        );
      }

      if (score === 0) {
        console.error(`Score is not available for address: ${address}`);
      }

      return {
        awsLongitude: coordinates?.[0] || null,
        awsLatitude: coordinates?.[1] || null,
        awsAddress: response.ResultItems[0].Title || '',
        awsAddressRegion: response.ResultItems[0].Address?.Region?.Name || '',
        awsScore: score,
      };
    } else {
      return null;
    }
  } catch (error) {
    console.error('Error fetching coordinates:', error);
    throw error;
  }
};

module.exports = {
  getCoordinatesForAddress,
};
