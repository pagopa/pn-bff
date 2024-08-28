const { expect } = require('chai');
const { checkLastDataDate } = require('../app/checkLastDate');

describe('checkLastDate', () => {
  let originalConsoleError = null;

  beforeEach(() => {
    // Save the original console.error function
    originalConsoleError = console.error;
    // Override console.error to a custom function
    console.error = function (message) {
      console.error.called = true;
      console.error.message = message;
    };
    console.error.called = false;
  });

  afterEach(() => {
    // Restore the original console.error function
    console.error = originalConsoleError;
  });

  it('should call console.error when the lastDate is older than the referenceDate', () => {
    const index = { lastDate: '2024-08-05' }; // pastDate
    const alarmNDays = '5';

    checkLastDataDate(index, alarmNDays);

    expect(console.error.called).to.be.true;
    expect(console.error.message).to.equal(
      `No data in the last ${alarmNDays} days. Last data date: ${index.lastDate}`
    );
  });

  it('should not call console.error when the lastDate is within the alarm days', () => {
    const index = { lastDate: new Date().toISOString().split('T')[0] }; // today
    const alarmNDays = '5';

    checkLastDataDate(index, alarmNDays);

    expect(console.error.called).to.be.false;
  });
});
