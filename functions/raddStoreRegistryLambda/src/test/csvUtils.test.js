const { validateCsvConfiguration, createCSVContent } = require('../app/csvUtils');
const chai = require('chai');
const expect = chai.expect;

describe('validateCsvConfiguration', () => {
  it('throws error when configuration is missing', () => {
    expect(() => validateCsvConfiguration()).throw('Configuration is missing');
  });

  it('throws error when configurationVersion is missing', () => {
    const config = { configs: [] };
    expect(() => validateCsvConfiguration(config)).throw('configurationVersion is missing');
  });

  it('throws error when configs is not an array', () => {
    const config = { configurationVersion: '1.0' };
    expect(() => validateCsvConfiguration(config)).throw('configs should be an array');
  });

  it('throws error when header is missing in configs', () => {
    const config = { configurationVersion: '1.0', configs: [{}] };
    expect(() => validateCsvConfiguration(config)).throw('Header is missing in csvConfiguration at index 0');
  });

  it('throws error when field is invalid in configs', () => {
    const config = { configurationVersion: '1.0', configs: [{ header: 'header', field: 'invalid' }] };
    expect(() => validateCsvConfiguration(config)).not.throw();
  });

  it('validates configuration successfully', () => {
    const config = { configurationVersion: '1.0', configs: [{ header: 'header', field: 'description' }] };
    expect(() => validateCsvConfiguration(config)).not.throw();
  });
});

describe('createCSVContent', () => {
  it('creates CSV content correctly', () => {
    const configs = [{ field: 'description' }, { field: 'city' }];
    const data = [{ description: 'desc1', city: 'city1' }, { description: 'desc2', city: 'city2' }];
    const expectedContent = 'desc1;city1\ndesc2;city2\n';
    expect(createCSVContent(configs, data)).equal(expectedContent);
  });

  it('handles missing fields in data correctly', () => {
    const configs = [{ field: 'description' }, { field: 'city' }];
    const data = [{ description: 'desc1' }, { description: 'desc2', city: 'city2' }];
    const expectedContent = 'desc1;\ndesc2;city2\n';
    expect(createCSVContent(configs, data)).equal(expectedContent);
  });
});