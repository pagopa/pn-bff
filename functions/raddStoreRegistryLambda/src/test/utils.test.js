const { checkIfIntervalPassed } = require('../app/utils');
const { expect } = require('chai');

describe('checkIfIntervalPassed', () => {
  beforeEach(() => {
    process.env.GENERATE_INTERVAL = '1';
  });

  it('returns true when interval has passed', () => {
    const date = new Date();
    date.setDate(date.getDate() - 2);
    console.log(date);
    const latestFile = { LastModified: new Date(date) };
    expect(checkIfIntervalPassed(latestFile)).to.be.true;
  });

  it('returns false when interval has not passed', () => {
    const latestFile = { LastModified: new Date() };
    expect(checkIfIntervalPassed(latestFile)).to.be.false;
  });

  it('throws error when GENERATE_INTERVAL is invalid', () => {
    process.env.GENERATE_INTERVAL = '-1';
    const latestFile = { LastModified: new Date() };
    expect(() => checkIfIntervalPassed(latestFile)).throw(
      'Invalid or missing GENERATE_INTERVAL environment variable'
    );
  });

  it('throws error when GENERATE_INTERVAL is missing', () => {
    delete process.env.GENERATE_INTERVAL;
    const latestFile = { LastModified: new Date() };
    expect(() => checkIfIntervalPassed(latestFile)).throw(
      'Invalid or missing GENERATE_INTERVAL environment variable'
    );
  });
});
