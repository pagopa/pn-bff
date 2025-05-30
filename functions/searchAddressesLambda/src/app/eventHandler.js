const { getAutocompleteAddresses } = require("./autocomplete");
const { getPlaceCoordinates } = require("./getPlace");

async function handleEvent(event) {
  const { address, placeId } = event;
  try {
    if (address) {
      const response = await getAutocompleteAddresses(address);
      return response;
    }

    if (placeId) {
      const response = await getPlaceCoordinates(placeId);
      return response;
    }

    // No address or placeId provided
    return null;
  } catch (error) {
    console.error("Error in handleEvent:", error);
    throw error;
  }
}

module.exports = { handleEvent };
