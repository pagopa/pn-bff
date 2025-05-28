const chai = require("chai");
const { mockClient } = require("aws-sdk-client-mock");
const {
  GeoPlacesClient,
  GetPlaceCommand,
  AutocompleteCommand,
} = require("@aws-sdk/client-geo-places");
const { handleEvent } = require("../app/eventHandler");

const expect = chai.expect;

describe("handleEvent tests", function () {
  let placesClientMock;

  beforeEach(() => {
    placesClientMock = mockClient(GeoPlacesClient);
  });

  afterEach(() => {
    placesClientMock.reset();
  });

  it("should call getAutocompleteAddresses when address is provided", async () => {
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

    const event = { address: "Via Roma, Milano" };

    const result = await handleEvent(event);

    expect(placesClientMock.commandCalls(AutocompleteCommand)).to.have.lengthOf(
      1
    );
    expect(result).to.deep.equal({
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

  it("should call getPlaceCoordinates when placeId is provided", async () => {
    const mockResponse = {
      Position: [9.19, 45.4642],
    };
    placesClientMock.on(GetPlaceCommand).resolves(mockResponse);
    const event = { placeId: "place-123" };

    const result = await handleEvent(event);

    expect(placesClientMock.commandCalls(GetPlaceCommand)).to.have.lengthOf(1);
    expect(result).to.deep.equal({
      longitude: 9.19,
      latitude: 45.4642,
    });
  });

  it("should return null when neither address nor placeId is provided", async () => {
    const event = {};

    const result = await handleEvent(event);

    expect(placesClientMock.commandCalls(AutocompleteCommand)).to.have.lengthOf(
      0
    );
    expect(placesClientMock.commandCalls(GetPlaceCommand)).to.have.lengthOf(0);
    expect(result).to.be.null;
  });

  it("should throw error when Autocomplete API call fails", async () => {
    const testError = new Error("AWS Autocomplete API failure");
    placesClientMock.on(AutocompleteCommand).rejects(testError);

    const event = { address: "Test Address" };

    try {
      await handleEvent(event);
      expect.fail("Expected function to throw an error");
    } catch (error) {
      expect(error).to.equal(testError);
    }
  });

  it("should throw error when GetPlace API call fails", async () => {
    const testError = new Error("AWS GetPlace API failure");
    placesClientMock.on(GetPlaceCommand).rejects(testError);

    const event = { placeId: "place-123" };

    try {
      await handleEvent(event);
      expect.fail("Expected function to throw an error");
    } catch (error) {
      expect(error).to.equal(testError);
    }
  });
});
