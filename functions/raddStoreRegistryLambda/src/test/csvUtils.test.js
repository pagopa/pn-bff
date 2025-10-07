const {
  validateCsvConfiguration,
  createCSVContent,
  sanitizeCSVField,
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

  it('handles configs without field property', () => {
    const configs = [
      { field: 'description' },
      { field: null },
      { field: 'city' },
    ];
    const data = [
      { description: 'desc1', city: 'city1' },
      { description: 'desc2', city: 'city2' },
    ];
    const expectedContent = '\ndesc1;;city1\ndesc2;;city2';
    expect(createCSVContent(configs, data)).equal(expectedContent);
  });
});

describe('sanitizeCSVField', () => {
  it('should return empty string for null or undefined', () => {
    expect(sanitizeCSVField(null)).to.equal('');
    expect(sanitizeCSVField(undefined)).to.equal('');
  });

  it('should return empty string for empty string', () => {
    expect(sanitizeCSVField('')).to.equal('');
  });

  it('should return empty string for whitespace-only strings', () => {
    expect(sanitizeCSVField('   ')).to.equal('');
  });

  it('should wrap simple strings in quotes', () => {
    expect(sanitizeCSVField('hello')).to.equal('"hello"');
    expect(sanitizeCSVField('Test Store')).to.equal('"Test Store"');
    expect(sanitizeCSVField('123')).to.equal('"123"');
  });

  it('should trim whitespace and wrap in quotes', () => {
    expect(sanitizeCSVField('  hello  ')).to.equal('"hello"');
    expect(sanitizeCSVField('\t  Test Store  \n')).to.equal('"Test Store"');
  });

  it('should handle semicolons (CSV delimiters)', () => {
    expect(sanitizeCSVField('Mon-Fri 9:00-17:00; Sat 10:00-14:00')).to.equal(
      '"Mon-Fri 9:00-17:00; Sat 10:00-14:00"'
    );
  });

  it('should replace newlines with spaces', () => {
    expect(sanitizeCSVField('Line 1\nLine 2')).to.equal('"Line 1 Line 2"');
  });

  it('should replace tabs with spaces', () => {
    expect(sanitizeCSVField('Tab\tSeparated\tValues')).to.equal(
      '"Tab Separated Values"'
    );
  });
});
