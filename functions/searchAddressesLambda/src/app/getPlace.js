const {
  GetPlaceCommand,
  GeoPlacesClient,
} = require("@aws-sdk/client-geo-places");

const client = new GeoPlacesClient({
  region: process.env.AWS_LOCATION_REGION,
});

/**
 * Get coordinates for a given PlaceId using AWS GetPlace API
 * @param placeId - The PlaceId to get coordinates for
 * @returns An object containing longitude/latitude coordinates, or null if not found
 */
const getPlaceCoordinates = async (placeId) => {
  const params = {
    PlaceId: placeId,
    Language: "it",
  };

  try {
    const command = new GetPlaceCommand(params);
    const response = await client.send(command);

    if (response.Position && response.Position.length >= 2) {
      return {
        longitude: response.Position[0],
        latitude: response.Position[1],
      };
    } else {
      return null;
    }
  } catch (error) {
    console.error("Error fetching getPlace:", error);
    throw error;
  }
};

module.exports = {
  getPlaceCoordinates,
};
