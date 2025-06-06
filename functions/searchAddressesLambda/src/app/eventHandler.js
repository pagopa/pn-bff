const { getAutocompleteAddresses } = require("./autocomplete");
const { getPlaceCoordinates } = require("./getPlace");
const {
  createSuccessResponse,
  createErrorResponse,
} = require("../utils/response");
const {
  SEARCH_ADDRESSES_URL,
  GET_PLACE_COORDINATES_URL,
} = require("../utils/urls");

async function handleEvent(event, context) {
  try {
    const resource = event.resource;
    const queryParams = event.queryStringParameters || {};

    if (resource === SEARCH_ADDRESSES_URL) {
      const { address } = queryParams;

      if (!address) {
        return createErrorResponse(
          400,
          "Missing required parameter: address",
          event,
          context
        );
      }

      const response = await getAutocompleteAddresses(address);
      return createSuccessResponse(response);
    }

    if (resource === GET_PLACE_COORDINATES_URL) {
      const { placeId } = queryParams;

      if (!placeId) {
        return createErrorResponse(
          400,
          "Missing required parameter: placeId",
          event,
          context
        );
      }

      const response = await getPlaceCoordinates(placeId);
      return createSuccessResponse(response);
    }

    return createErrorResponse(
      404,
      `Route not found: ${resource}`,
      event,
      context
    );
  } catch (error) {
    console.error("Error in handleEvent:", error);

    if (error.name === "ValidationException") {
      return createErrorResponse(
        400,
        `Invalid input: ${error.message}`,
        event,
        context
      );
    }

    if (error.name === "AccessDeniedException") {
      return createErrorResponse(
        403,
        "Access denied to AWS Location service",
        event,
        context
      );
    }

    if (error.name === "ThrottlingException") {
      return createErrorResponse(
        429,
        "Too many requests to AWS Location service",
        event,
        context
      );
    }

    return createErrorResponse(
      500,
      "AWS Location service is unavailable",
      event,
      context
    );
  }
}

module.exports = { handleEvent };
