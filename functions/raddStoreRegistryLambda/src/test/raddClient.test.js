const { expect } = require('chai');
const sinon = require('sinon');
const axios = require('axios');
const MockAdapter = require('axios-mock-adapter');
const { fetchApi } = require('../app/raddClient');
 
describe('fetchApi', () => {
    let mock;
 
    beforeEach(() => {
        mock = new MockAdapter(axios);
    });
 
    afterEach(() => {
        mock.restore();
        sinon.restore();
    });
 
    it('should fetch API data with default parameters', async () => {
        const mockData = {
            registries: [
                {
                    description: "Sportello ABC",
                    address: {
                        addressRow: "Via Roma 123",
                        cap: "00100",
                        city: "Roma",
                        pr: "RM",
                        country: "Italia"
                    },
                    phoneNumber: "+39 0123456789",
                    geoLocation: {
                        latitude: "41.9028",
                        longitude: "12.4964"
                    },
                    openingTime: "mon=10:00-13:00_14:00-20:00#tue=10:00-20:00#",
                    externalCode: "123456",
                    capacity: 100
                }
            ],
            lastKey: "someLastKey"
        };
        mock.onGet(`${process.env.RADD_STORE_REGISTRY_API_URL}`).reply(200, mockData);
 
        const result = await fetchApi();
        expect(result).to.deep.equal(mockData);
        expect(mock.history.get[0].params).to.deep.equal({ limit: 1000, lastKey: null });
    });
 
    it('should fetch API data with specified parameters', async () => {
        const mockData = {
            registries: [
                {
                    description: "Sportello DEF",
                    address: {
                        addressRow: "Via Milano 456",
                        cap: "20100",
                        city: "Milano",
                        pr: "MI",
                        country: "Italia"
                    },
                    phoneNumber: "+39 0987654321",
                    geoLocation: {
                        latitude: "45.4642",
                        longitude: "9.19"
                    },
                    openingTime: "wed=09:00-12:00#thu=09:00-17:00#",
                    externalCode: "654321",
                    capacity: 150
                }
            ],
            lastKey: "anotherLastKey"
        };
        mock.onGet(`${process.env.RADD_STORE_REGISTRY_API_URL}`).reply(200, mockData);
 
        const result = await fetchApi('lastKey123', 500);
        expect(result).to.deep.equal(mockData);
        expect(mock.history.get[0].params).to.deep.equal({ limit: 500, lastKey: 'lastKey123' });
    });
 
    it('should throw an error when API request fails', async () => {
        mock.onGet(`${process.env.RADD_STORE_REGISTRY_API_URL}`).reply(500);
 
        try {
            await fetchApi();
        } catch (error) {
            expect(error.message).to.equal('Failed to fetch data from API');
        }
    });
});