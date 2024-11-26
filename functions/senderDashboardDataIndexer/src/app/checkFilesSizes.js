/**
 * Function to check if the file size is less than the minimum size.
 * If the file size is less than the minimum size, an error is thrown.
 * @param {*} index - Index object
 * @param {*} minBytesDataLakeFile - Minimum size of the file in bytes
 */
const checkFilesSizes = (index, minBytesDataLakeFile) => {
  if (
    index.overviewObjectSizeByte < minBytesDataLakeFile ||
    index.focusObjectSizeByte < minBytesDataLakeFile
  ) {
    const errorMessage = `File size is less than the minimum size: ${minBytesDataLakeFile} bytes`;
    console.error(errorMessage);
    throw new Error(errorMessage);
  }
};

module.exports = { checkFilesSizes };
