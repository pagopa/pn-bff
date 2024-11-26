const { expect } = require('chai');
const { checkFilesSizes } = require('../app/checkFilesSizes');

describe('checkFilesSizes', () => {
  const minSize = 1000;

  it('should not throw an error if both file sizes are above or equal to the minimum size', () => {
    const index = {
      overviewObjectSizeByte: 1500,
      focusObjectSizeByte: 2000,
    };
    expect(() => checkFilesSizes(index, minSize)).to.not.throw();
  });

  it('should throw an error if overviewObjectSizeByte is below the minimum size', () => {
    const index = {
      overviewObjectSizeByte: 500,
      focusObjectSizeByte: 1500,
    };
    expect(() => checkFilesSizes(index, minSize)).to.throw(
      `File size is less than the minimum size: ${minSize} bytes`
    );
  });

  it('should throw an error if focusObjectSizeByte is below the minimum size', () => {
    const index = {
      overviewObjectSizeByte: 1500,
      focusObjectSizeByte: 500,
    };
    expect(() => checkFilesSizes(index, minSize)).to.throw(
      `File size is less than the minimum size: ${minSize} bytes`
    );
  });

  it('should throw an error if both file sizes are below the minimum size', () => {
    const index = {
      overviewObjectSizeByte: 500,
      focusObjectSizeByte: 500,
    };
    expect(() => checkFilesSizes(index, minSize)).to.throw(
      `File size is less than the minimum size: ${minSize} bytes`
    );
  });
});
