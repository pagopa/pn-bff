const {
  validateCsvConfiguration,
  createCSVContent,
  addRowToWrongAddressCSV,
  wrongAddressesCsvHeader,
} = require('../app/csvUtils');
const chai = require('chai');
const expect = chai.expect;

describe('validateCsvConfiguration', () => {
  it('throws error when configuration is missing', () => {
    expect(() => validateCsvConfiguration()).throw('Configuration is missing');
  });

  it('throws error when configurationVersion is missing', () => {
    const config = { configs: [] };
    expect(() => validateCsvConfiguration(config)).throw(
      'configurationVersion is missing'
    );
  });

  it('throws error when configs is not an array', () => {
    const config = { configurationVersion: '1.0' };
    expect(() => validateCsvConfiguration(config)).throw(
      'configs should be an array'
    );
  });

  it('throws error when header is missing in configs', () => {
    const config = { configurationVersion: '1.0', configs: [{}] };
    expect(() => validateCsvConfiguration(config)).throw(
      'Header is missing in csvConfiguration at index 0'
    );
  });

  it('throws error when field is invalid in configs', () => {
    const config = {
      configurationVersion: '1.0',
      configs: [{ header: 'header', field: 'invalid' }],
    };
    expect(() => validateCsvConfiguration(config)).not.throw();
  });

  it('validates configuration successfully', () => {
    const config = {
      configurationVersion: '1.0',
      configs: [{ header: 'header', field: 'description' }],
    };
    expect(() => validateCsvConfiguration(config)).not.throw();
  });
});

describe('createCSVContent', () => {
  it('creates CSV content correctly', () => {
    const configs = [{ field: 'description' }, { field: 'city' }];
    const data = [
      { description: 'desc1', city: 'city1' },
      { description: 'desc2', city: 'city2' },
    ];
    const expectedContent = '\ndesc1;city1\ndesc2;city2';
    expect(createCSVContent(configs, data)).equal(expectedContent);
  });

  it('handles missing fields in data correctly', () => {
    const configs = [{ field: 'description' }, { field: 'city' }];
    const data = [
      { description: 'desc1' },
      { description: 'desc2', city: 'city2' },
    ];
    const expectedContent = '\ndesc1;\ndesc2;city2';
    expect(createCSVContent(configs, data)).equal(expectedContent);
  });
});

describe('wrongAddressesCsvHeader', () => {
  it('should return the correct header string', () => {
    const expectedHeader =
      'descrizione;indirizzo;citta;provincia;indirizzo AWS;score AWS;latitudine;longitudine';
    expect(wrongAddressesCsvHeader).to.equal(expectedHeader);
  });
});

describe('addRowToWrongAddressCSV', () => {
  const sampleRegistry = {
    description: 'CAF CGIL',
    address: 'Via Roma 10',
    city: 'Roma',
    province: 'RM',
    awsAddress: 'Via Roma 10, 20100 RM, ROMA',
    awsScore: '0.9',
    awsLatitude: '41.9028',
    awsLongitude: '12.4964',
  };

  it('should add a row to the wrongAddressArray with correct values', () => {
    const wrongAddressArray = [];
    addRowToWrongAddressCSV(sampleRegistry, wrongAddressArray);

    const expectedRow =
      'CAF CGIL;Via Roma 10;Roma;RM;Via Roma 10, 20100 RM, ROMA;0.9;41.9028;12.4964';
    expect(wrongAddressArray.length).to.equal(1);
    expect(wrongAddressArray[0]).to.equal(expectedRow);
  });

  it('should handle missing data fields by inserting empty strings', () => {
    // missing city and coordinates
    const { city, awsLatitude, awsLongitude, ...incompleteData } =
      sampleRegistry;

    const wrongAddressArray = [];
    addRowToWrongAddressCSV(incompleteData, wrongAddressArray);

    const expectedRow =
      'CAF CGIL;Via Roma 10;;RM;Via Roma 10, 20100 RM, ROMA;0.9;;';
    expect(wrongAddressArray.length).to.equal(1);
    expect(wrongAddressArray[0]).to.equal(expectedRow);
  });

  it('should append multiple rows correctly', () => {
    const sampleRegistry2 = {
      ...sampleRegistry,
      description: 'CAF UIL',
      address: 'Via Nazionale 15',
      awsAddress: 'Via Nazionale 15, 20100 RM, ROMA',
      awsLatitude: '42.6741',
      awsLongitude: '11.9082',
    };

    const wrongAddressArray = [];
    addRowToWrongAddressCSV(sampleRegistry, wrongAddressArray);
    addRowToWrongAddressCSV(sampleRegistry2, wrongAddressArray);

    expect(wrongAddressArray.length).to.equal(2);
    expect(wrongAddressArray[0]).to.equal(
      'CAF CGIL;Via Roma 10;Roma;RM;Via Roma 10, 20100 RM, ROMA;0.9;41.9028;12.4964'
    );
    expect(wrongAddressArray[1]).to.equal(
      'CAF UIL;Via Nazionale 15;Roma;RM;Via Nazionale 15, 20100 RM, ROMA;0.9;42.6741;11.9082'
    );
  });
});
