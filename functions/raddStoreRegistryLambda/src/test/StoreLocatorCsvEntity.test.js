const { expect } = require('chai');
const sinon = require('sinon');
const AWS = require('aws-sdk-client-mock');
const { mapApiResponseToStoreLocatorCsvEntities } = require('../app/StoreLocatorCsvEntity');
const StoreLocatorCsvEntity = require('../app/StoreLocatorCsvEntity').StoreLocatorCsvEntity; // Assumendo che tu abbia esportato anche la classe StoreLocatorCsvEntity
 
describe('StoreLocatorCsvEntity', () => {
    afterEach(() => {
        AWS.restore();
        sinon.restore();
    });
 
    it('should map API response correctly', () => {
        const registry = {
            description: "Test Store",
            address: {
                city: "Test City",
                addressRow: "123 Test St",
                pr: "Test Province",
                cap: "12345"
            },
            phoneNumber: "123/456/7890",
            openingTime: "MON 09:00-17:00#TUE 09:00-17:00#WED 09:00-17:00#THU 09:00-17:00#FRI 09:00-17:00#SAT 10:00-14:00#SUN closed",
            geoLocation: {
                latitude: "12.345678",
                longitude: "98.765432"
            }
        };
 
        const result = mapApiResponseToStoreLocatorCsvEntities(registry);
 
        expect(result.description).to.equal("Test Store");
        expect(result.city).to.equal("Test City");
        expect(result.address).to.equal("123 Test St");
        expect(result.province).to.equal("Test Province");
        expect(result.zipCode).to.equal("12345");
        expect(result.phoneNumber).to.equal("123 456 7890");
        expect(result.monday).to.equal("09:00-17:00");
        expect(result.tuesday).to.equal("09:00-17:00");
        expect(result.wednesday).to.equal("09:00-17:00");
        expect(result.thursday).to.equal("09:00-17:00");
        expect(result.friday).to.equal("09:00-17:00");
        expect(result.saturday).to.equal("10:00-14:00");
        expect(result.sunday).to.equal("closed");
        expect(result.latitude).to.equal("12.345678");
        expect(result.longitude).to.equal("98.765432");
    });
 
    it('should handle missing optional fields gracefully', () => {
        const registry = {
            description: "Test Store",
            address: {
                city: "Test City",
                addressRow: "123 Test St",
                pr: "Test Province",
                cap: "12345"
            },
            phoneNumber: "123/456/7890"
        };
 
        const result = mapApiResponseToStoreLocatorCsvEntities(registry);
 
        expect(result.description).to.equal("Test Store");
        expect(result.city).to.equal("Test City");
        expect(result.address).to.equal("123 Test St");
        expect(result.province).to.equal("Test Province");
        expect(result.zipCode).to.equal("12345");
        expect(result.phoneNumber).to.equal("123 456 7890");
        expect(result.monday).to.equal("");
        expect(result.tuesday).to.equal("");
        expect(result.wednesday).to.equal("");
        expect(result.thursday).to.equal("");
        expect(result.friday).to.equal("");
        expect(result.saturday).to.equal("");
        expect(result.sunday).to.equal("");
        expect(result.latitude).to.equal("");
        expect(result.longitude).to.equal("");
    });
 
    it('should handle null values correctly', () => {
        const registry = {
            description: null,
            address: null,
            phoneNumber: null,
            openingTime: null,
            geoLocation: null
        };
 
        const result = mapApiResponseToStoreLocatorCsvEntities(registry);
 
        expect(result.description).to.equal("");
        expect(result.city).to.equal("");
        expect(result.address).to.equal("");
        expect(result.province).to.equal("");
        expect(result.zipCode).to.equal("");
        expect(result.phoneNumber).to.equal("");
        expect(result.monday).to.equal("");
        expect(result.tuesday).to.equal("");
        expect(result.wednesday).to.equal("");
        expect(result.thursday).to.equal("");
        expect(result.friday).to.equal("");
        expect(result.saturday).to.equal("");
        expect(result.sunday).to.equal("");
        expect(result.latitude).to.equal("");
        expect(result.longitude).to.equal("");
    });
});