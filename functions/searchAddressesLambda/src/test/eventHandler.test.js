const chai = require("chai");
const { mockClient } = require("aws-sdk-client-mock");
const {
  GeoPlacesClient,
  GetPlaceCommand,
  AutocompleteCommand,
} = require("@aws-sdk/client-geo-places");
const { handleEvent } = require("../app/eventHandler");
const {
  SEARCH_ADDRESSES_URL,
  GET_PLACE_COORDINATES_URL,
} = require("../utils/urls");

const expect = chai.expect;

describe("handleEvent tests", function () {
  let placesClientMock;

  const context = { awsRequestId: "test-request-id" };

  beforeEach(() => {
    placesClientMock = mockClient(GeoPlacesClient);
  });

  afterEach(() => {
    placesClientMock.reset();
  });

  describe("SearchAddress tests", () => {
    it("should call getAutocompleteAddresses on /searchAddress", async () => {
      const mockResponse = {
        ResultItems: [
          {
            PlaceId: "place-123",
            PlaceType: "Street",
            Address: {
              Label: "Via Roma 1, Milano MI, Italy",
              Street: "Via Roma 1",
              SubRegion: { Code: "MI" },
              Locality: "Milano",
            },
          },
        ],
      };
      placesClientMock.on(AutocompleteCommand).resolves(mockResponse);

      const event = {
        resource: SEARCH_ADDRESSES_URL,
        queryStringParameters: {
          address: "Via Roma, Milano",
        },
      };

      const result = await handleEvent(event, context);

      expect(
        placesClientMock.commandCalls(AutocompleteCommand)
      ).to.have.lengthOf(1);
      expect(result.statusCode).to.equal(200);
      expect(JSON.parse(result.body)).to.deep.equal({
        data: [
          {
            placeId: "place-123",
            placeType: "Street",
            label: "Via Roma 1, Milano MI, Italy",
            street: "Via Roma 1",
            province: "MI",
            city: "Milano",
          },
        ],
      });
    });

    it("should return 400 error when address parameter is missing for /searchAddress", async () => {
      const event = {
        resource: SEARCH_ADDRESSES_URL,
        queryStringParameters: {},
      };

      const result = await handleEvent(event, context);

      expect(result.statusCode).to.equal(400);

      const responseBody = JSON.parse(result.body);
      expect(responseBody.status).to.equal(400);
      expect(responseBody.title).to.equal("ERROR");
      expect(responseBody.traceId).to.equal("test-request-id");
      expect(responseBody.errors).to.have.lengthOf(1);
      expect(responseBody.errors[0].code).to.equal("PN_LOCATION_ERROR_400");
      expect(responseBody.errors[0].detail).to.equal(
        "Missing required parameter: address"
      );
    });

    it("should return 400 error when placeId parameter is missing for /getPlaceCoordinates", async () => {
      const event = {
        resource: GET_PLACE_COORDINATES_URL,
        queryStringParameters: {},
      };

      const result = await handleEvent(event, context);

      expect(result.statusCode).to.equal(400);

      const responseBody = JSON.parse(result.body);
      expect(responseBody.status).to.equal(400);
      expect(responseBody.title).to.equal("ERROR");
      expect(responseBody.traceId).to.equal("test-request-id");
      expect(responseBody.errors).to.have.lengthOf(1);
      expect(responseBody.errors[0].code).to.equal("PN_LOCATION_ERROR_400");
      expect(responseBody.errors[0].detail).to.equal(
        "Missing required parameter: placeId"
      );
    });
  });

  describe("GetPlaceCoordinates tests", () => {
    it("should call getPlaceCoordinates on /getPlaceCoordinates", async () => {
      const mockResponse = {
        Position: [9.19, 45.4642],
      };
      placesClientMock.on(GetPlaceCommand).resolves(mockResponse);

      const event = {
        resource: GET_PLACE_COORDINATES_URL,
        queryStringParameters: {
          placeId: "place-123",
        },
      };

      const result = await handleEvent(event, context);

      expect(placesClientMock.commandCalls(GetPlaceCommand)).to.have.lengthOf(
        1
      );
      expect(result.statusCode).to.equal(200);
      expect(JSON.parse(result.body)).to.deep.equal({
        longitude: 9.19,
        latitude: 45.4642,
      });
    });
  });

  describe("Error handling", () => {
    it("should return 404 error when unknown resource is requested", async () => {
      const event = {
        resource: "/unknownResource",
        queryStringParameters: {},
      };

      const result = await handleEvent(event, context);

      expect(
        placesClientMock.commandCalls(AutocompleteCommand)
      ).to.have.lengthOf(0);
      expect(placesClientMock.commandCalls(GetPlaceCommand)).to.have.lengthOf(
        0
      );
      expect(result.statusCode).to.equal(404);

      const responseBody = JSON.parse(result.body);
      expect(responseBody.status).to.equal(404);
      expect(responseBody.title).to.equal("ERROR");
      expect(responseBody.traceId).to.equal("test-request-id");
      expect(responseBody.errors).to.have.lengthOf(1);
      expect(responseBody.errors[0].code).to.equal("PN_LOCATION_ERROR_404");
      expect(responseBody.errors[0].detail).to.include("Route not found");
    });

    it("should handle ValidationException from AWS Location service", async () => {
      const validationError = new Error("Invalid input");
      validationError.name = "ValidationException";
      placesClientMock.on(AutocompleteCommand).rejects(validationError);

      const event = {
        resource: SEARCH_ADDRESSES_URL,
        queryStringParameters: {
          address: "invalid address",
        },
      };

      const result = await handleEvent(event, context);

      expect(result.statusCode).to.equal(400);

      const responseBody = JSON.parse(result.body);
      expect(responseBody.status).to.equal(400);
      expect(responseBody.title).to.equal("ERROR");
      expect(responseBody.traceId).to.equal("test-request-id");
      expect(responseBody.errors).to.have.lengthOf(1);
      expect(responseBody.errors[0].code).to.equal("PN_LOCATION_ERROR_400");
      expect(responseBody.errors[0].detail).to.equal(
        "Invalid input: Invalid input"
      );
    });

    it("should handle AccessDeniedException from AWS Location service", async () => {
      const accessError = new Error("Access denied");
      accessError.name = "AccessDeniedException";
      placesClientMock.on(AutocompleteCommand).rejects(accessError);

      const event = {
        resource: SEARCH_ADDRESSES_URL,
        queryStringParameters: {
          address: "test address",
        },
      };

      const result = await handleEvent(event, context);

      expect(result.statusCode).to.equal(403);

      const responseBody = JSON.parse(result.body);
      expect(responseBody.status).to.equal(403);
      expect(responseBody.title).to.equal("ERROR");
      expect(responseBody.traceId).to.equal("test-request-id");
      expect(responseBody.errors).to.have.lengthOf(1);
      expect(responseBody.errors[0].code).to.equal("PN_LOCATION_ERROR_403");
      expect(responseBody.errors[0].detail).to.equal(
        "Access denied to AWS Location service"
      );
    });

    it("should handle ThrottlingException from AWS Location service", async () => {
      const throttleError = new Error("Too many requests");
      throttleError.name = "ThrottlingException";
      placesClientMock.on(GetPlaceCommand).rejects(throttleError);

      const event = {
        resource: GET_PLACE_COORDINATES_URL,
        queryStringParameters: {
          placeId: "place-123",
        },
      };

      const result = await handleEvent(event, context);

      expect(result.statusCode).to.equal(429);

      const responseBody = JSON.parse(result.body);
      expect(responseBody.status).to.equal(429);
      expect(responseBody.title).to.equal("ERROR");
      expect(responseBody.traceId).to.equal("test-request-id");
      expect(responseBody.errors).to.have.lengthOf(1);
      expect(responseBody.errors[0].code).to.equal("PN_LOCATION_ERROR_429");
      expect(responseBody.errors[0].detail).to.equal(
        "Too many requests to AWS Location service"
      );
    });

    it("should handle generic errors with 500 status", async () => {
      const genericError = new Error("Unknown error");
      placesClientMock.on(AutocompleteCommand).rejects(genericError);

      const event = {
        resource: SEARCH_ADDRESSES_URL,
        queryStringParameters: {
          address: "test address",
        },
      };

      const result = await handleEvent(event, context);

      expect(result.statusCode).to.equal(500);

      const responseBody = JSON.parse(result.body);
      expect(responseBody.status).to.equal(500);
      expect(responseBody.title).to.equal("ERROR");
      expect(responseBody.traceId).to.equal("test-request-id");
      expect(responseBody.errors).to.have.lengthOf(1);
      expect(responseBody.errors[0].code).to.equal("PN_LOCATION_ERROR_500");
      expect(responseBody.errors[0].detail).to.equal(
        "AWS Location service is unavailable"
      );
    });

    it("should handle missing queryStringParameters gracefully", async () => {
      const event = {
        resource: SEARCH_ADDRESSES_URL,
      };

      const result = await handleEvent(event, context);

      expect(result.statusCode).to.equal(400);

      const responseBody = JSON.parse(result.body);
      expect(responseBody.status).to.equal(400);
      expect(responseBody.title).to.equal("ERROR");
      expect(responseBody.traceId).to.equal("test-request-id");
      expect(responseBody.errors).to.have.lengthOf(1);
      expect(responseBody.errors[0].code).to.equal("PN_LOCATION_ERROR_400");
      expect(responseBody.errors[0].detail).to.equal(
        "Missing required parameter: address"
      );
    });
  });
});
