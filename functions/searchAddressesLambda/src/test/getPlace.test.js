const chai = require("chai");
const { mockClient } = require("aws-sdk-client-mock");
const {
  GeoPlacesClient,
  GetPlaceCommand,
} = require("@aws-sdk/client-geo-places");
const { getPlaceCoordinates } = require("../app/getPlace");

const expect = chai.expect;

describe("getPlace tests", function () {
  let placesClientMock;

  beforeEach(() => {
    placesClientMock = mockClient(GeoPlacesClient);
  });

  afterEach(() => {
    placesClientMock.reset();
  });

  it("should return coordinates when API returns valid position", async () => {
    const mockResponse = {
      Position: [9.19, 45.4642],
    };
    placesClientMock.on(GetPlaceCommand).resolves(mockResponse);

    const result = await getPlaceCoordinates("place-123");

    expect(result).to.deep.equal({
      longitude: 9.19,
      latitude: 45.4642,
    });
  });

  it("should call GetPlaceCommand with correct parameters", async () => {
    const mockResponse = { Position: [9.19, 45.4642] };
    placesClientMock.on(GetPlaceCommand).resolves(mockResponse);

    await getPlaceCoordinates("place-123");

    expect(placesClientMock.commandCalls(GetPlaceCommand)).to.have.lengthOf(1);
    expect(
      placesClientMock.commandCalls(GetPlaceCommand)[0].args[0].input
    ).to.deep.equal({
      PlaceId: "place-123",
      Language: "it",
    });
  });

  it("should return null when API returns empty position array", async () => {
    const mockResponse = {
      Position: [],
    };
    placesClientMock.on(GetPlaceCommand).resolves(mockResponse);

    const result = await getPlaceCoordinates("place-123");

    expect(result).to.be.null;
  });

  it("should return null when Position is undefined", async () => {
    const mockResponse = {};
    placesClientMock.on(GetPlaceCommand).resolves(mockResponse);

    const result = await getPlaceCoordinates("place-123");

    expect(result).to.be.null;
  });

  it("should throw error when API call fails", async () => {
    // Arrange
    const errorMessage = "Place not found";
    placesClientMock.on(GetPlaceCommand).rejects(new Error(errorMessage));

    // Act & Assert
    try {
      await getPlaceCoordinates("place-123");
      expect.fail("Expected function to throw an error");
    } catch (error) {
      expect(error.message).to.equal(errorMessage);
    }
  });
});
