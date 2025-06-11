const chai = require("chai");
const { mockClient } = require("aws-sdk-client-mock");
const {
  GeoPlacesClient,
  AutocompleteCommand,
} = require("@aws-sdk/client-geo-places");
const { getAutocompleteAddresses } = require("../app/autocomplete");

const expect = chai.expect;

describe("autocomplete tests", function () {
  let placesClientMock;

  beforeEach(() => {
    placesClientMock = mockClient(GeoPlacesClient);
  });

  afterEach(() => {
    placesClientMock.reset();
  });

  it("should return formatted addresses when API returns results", async () => {
    const mockResponse = {
      ResultItems: [
        {
          PlaceId: "place-123",
          PlaceType: "Street",
          Address: {
            Label: "Via Roma 1, 20121 Milano MI, Italy",
            Street: "Via Roma 1",
            SubRegion: {
              Code: "MI",
            },
            Locality: "Milano",
          },
        },
        {
          PlaceId: "place-456",
          PlaceType: "Municipality",
          Address: {
            Label: "Via Garibaldi 5, 10121 Torino TO, Italy",
            Street: "Via Garibaldi 5",
            SubRegion: {
              Code: "TO",
            },
            Locality: "Torino",
          },
        },
      ],
    };

    placesClientMock.on(AutocompleteCommand).resolves(mockResponse);

    const result = await getAutocompleteAddresses("Via Roma");

    expect(result).to.deep.equal({
      data: [
        {
          placeId: "place-123",
          placeType: "Street",
          address: {
            Label: "Via Roma 1, 20121 Milano MI, Italy",
            Street: "Via Roma 1",
            SubRegion: {
              Code: "MI",
            },
            Locality: "Milano",
          },
        },
        {
          placeId: "place-456",
          placeType: "Municipality",
          address: {
            Label: "Via Garibaldi 5, 10121 Torino TO, Italy",
            Street: "Via Garibaldi 5",
            SubRegion: {
              Code: "TO",
            },
            Locality: "Torino",
          },
        },
      ],
    });
  });

  it("should call AutocompleteCommand with correct parameters", async () => {
    const mockResponse = { ResultItems: [] };
    placesClientMock.on(AutocompleteCommand).resolves(mockResponse);

    await getAutocompleteAddresses("Test Address");

    expect(placesClientMock.commandCalls(AutocompleteCommand)).to.have.lengthOf(
      1
    );
    expect(
      placesClientMock.commandCalls(AutocompleteCommand)[0].args[0].input
    ).to.deep.equal({
      QueryText: "Test Address",
      MaxResults: 5,
      Filter: {
        IncludeCountries: ["ITA"],
      },
      AdditionalFeatures: ["Core"],
      Language: "it",
    });
  });

  it("should return empty data array when API returns no results", async () => {
    const mockResponse = {
      ResultItems: [],
    };
    placesClientMock.on(AutocompleteCommand).resolves(mockResponse);

    const result = await getAutocompleteAddresses("Nonexistent Street");

    expect(result).to.deep.equal({
      data: [],
    });
  });

  it("should return empty data array when ResultItems is undefined", async () => {
    const mockResponse = {};
    placesClientMock.on(AutocompleteCommand).resolves(mockResponse);

    const result = await getAutocompleteAddresses("Some Address");

    expect(result).to.deep.equal({
      data: [],
    });
  });

  it("should throw error when API call fails", async () => {
    const errorMessage = "AWS API Error";
    placesClientMock.on(AutocompleteCommand).rejects(new Error(errorMessage));

    try {
      await getAutocompleteAddresses("Test Address");
    } catch (error) {
      expect(error.message).to.equal(errorMessage);
    }
  });
});
