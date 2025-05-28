const {
  AutocompleteCommand,
  GeoPlacesClient,
} = require("@aws-sdk/client-geo-places");

const client = new GeoPlacesClient({
  region: process.env.AWS_LOCATION_REGION,
});

/**
 * Get addresses results for a given address using AWS Autocomplete API
 * @param address - The address to search
 * @returns An object containing the addresses or an empty array
 */
const getAutocompleteAddresses = async (address) => {
  const params = {
    QueryText: address,
    MaxResults: 5,
    Filter: {
      IncludeCountries: ["ITA"],
    },
    AdditionalFeatures: ["Core"],
    Language: "it",
  };

  try {
    const command = new AutocompleteCommand(params);
    const response = await client.send(command);

    if (response.ResultItems && response.ResultItems.length > 0) {
      const data = response.ResultItems.map((item) => ({
        placeId: item.PlaceId,
        placeType: item.PlaceType,
        label: item.Address?.Label,
        street: item.Address?.Street,
        province: item.Address?.SubRegion?.Code,
        city: item.Address?.Locality,
      }));

      return { data };
    } else {
      return {
        data: [],
      };
    }
  } catch (error) {
    console.error("Error fetching autocomplete:", error);
    throw error;
  }
};

module.exports = {
  getAutocompleteAddresses,
};
