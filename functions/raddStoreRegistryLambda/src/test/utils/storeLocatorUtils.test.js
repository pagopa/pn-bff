const {
  getOpeningTimeByDay,
  isAWSAddressValid,
} = require('../../utils/storeLocatorUtils');
const { setupEnv } = require('./test.utils');
const chai = require('chai');

const expect = chai.expect;

describe('getOpeningTimeByDay', () => {
  it('should return array of 7 nulls when given empty object', () => {
    const result = getOpeningTimeByDay({});
    expect(result).to.deep.equal([null, null, null, null, null, null, null]);
    expect(result).to.have.length(7);
  });

  it('should map single day correctly', () => {
    const input = { lun: '09:00-18:00' };
    const result = getOpeningTimeByDay(input);
    expect(result[0]).to.equal('09:00-18:00');
    expect(result[1]).to.be.null;
    expect(result[2]).to.be.null;
    expect(result[3]).to.be.null;
    expect(result[4]).to.be.null;
    expect(result[5]).to.be.null;
    expect(result[6]).to.be.null;
  });

  it('should map all days correctly', () => {
    const input = {
      lun: '09:00-18:00',
      mar: '10:00-19:00',
      mer: '08:00-17:00',
      gio: '09:30-18:30',
      ven: '09:00-20:00',
      sab: '10:00-16:00',
      dom: '12:00-15:00',
    };
    const result = getOpeningTimeByDay(input);
    expect(result).to.deep.equal([
      '09:00-18:00',
      '10:00-19:00',
      '08:00-17:00',
      '09:30-18:30',
      '09:00-20:00',
      '10:00-16:00',
      '12:00-15:00',
    ]);
  });

  it('should handle mixed case day keys', () => {
    const input = {
      LUN: '09:00-18:00',
      Mar: '10:00-19:00',
      MER: '08:00-17:00',
      gio: '09:30-18:30',
    };
    const result = getOpeningTimeByDay(input);
    expect(result[0]).to.equal('09:00-18:00');
    expect(result[1]).to.equal('10:00-19:00');
    expect(result[2]).to.equal('08:00-17:00');
    expect(result[3]).to.equal('09:30-18:30');
  });

  it('should ignore unknown day keys', () => {
    const input = {
      lun: '09:00-18:00',
      invalidDay: '10:00-19:00',
      wed: '08:00-17:00',
      mer: '11:00-16:00',
    };
    const result = getOpeningTimeByDay(input);
    expect(result[0]).to.equal('09:00-18:00');
    expect(result[1]).to.be.null;
    expect(result[2]).to.equal('11:00-16:00');
    expect(result.filter((time) => time !== null)).to.have.length(2);
  });
});

describe('isAWSAddressValid', () => {
  beforeEach(() => {
    setupEnv();
  });

  it('should return true when all scores are perfect (1.0)', () => {
    const perfectScores = {
      addressNumber: 1,
      country: 1,
      locality: 1,
      postalCode: 1,
      subRegion: 1,
      overall: 1,
    };
    const result = isAWSAddressValid(perfectScores);
    expect(result).to.be.true;
  });

  it('should return true when all scores is over the thresholds', () => {
    const scores = {
      addressNumber: 0.9,
      country: 1,
      locality: 0.87,
      postalCode: 1,
      subRegion: 1,
      overall: 0.92,
    };
    const result = isAWSAddressValid(scores);
    expect(result).to.be.true;
  });

  it('should return false when a required score is missing', () => {
    // subRegion is missing
    const scores = {
      addressNumber: 1,
      locality: 1,
      postalCode: 1,
      overall: 1,
    };
    const result = isAWSAddressValid(scores);
    expect(result).to.be.false;
  });

  it('should return false when all required scores are 1 but overall is below threshold', () => {
    const scores = {
      addressNumber: 1,
      locality: 1,
      postalCode: 1,
      subRegion: 1,
      overall: 0.7,
    };
    const result = isAWSAddressValid(scores);
    expect(result).to.be.false;
  });

  it('should return false when scores is null', () => {
    const result = isAWSAddressValid(null);
    expect(result).to.be.false;
  });

  it('should return false when scores is undefined', () => {
    const result = isAWSAddressValid(undefined);
    expect(result).to.be.false;
  });

  it('should return false when overall is missing', () => {
    const scores = {
      addressNumber: 0.95,
      locality: 1,
      postalCode: 1,
      subRegion: 1,
    };
    const result = isAWSAddressValid(scores);
    expect(result).to.be.false;
  });
});
