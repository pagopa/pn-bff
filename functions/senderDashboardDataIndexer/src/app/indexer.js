const { streamLines, headObject } = require('./s3.js');
const { performance } = require('perf_hooks');

/**
 * Creates an index object containing the bytes range for each sender sender_id.
 *
 * @param {Object} s3Client The initialized S3 client.
 * @param {string} bucketName The name of the S3 bucket.
 * @param {string} objectKey The key of the S3 object.
 * @param {string} [versionId] Optional version ID of the object to fetch a
 * specific version.
 * @returns {Promise<Object>} A promise that resolves to an object containing
 * metadata about the process and sender details.
 */
const createIndexObject = async (
  s3Client,
  bucketName,
  objectKey,
  objectVersionId
) => {
  const start = performance.now();
  const genTimestamp = new Date().toISOString();

  if (!objectVersionId) {
    const metadata = await headObject(s3Client, bucketName, objectKey);
    console.log(metadata);
    objectVersionId = metadata.VersionId;
    console.log(objectVersionId);
  }

  const senders = {};
  let lastDate = '';
  let currentNumRows = 1;
  let currentSenderId;

  // Objects are ordered by sender_id
  const { bytes } = await streamLines(
    s3Client,
    bucketName,
    objectKey,
    (obj, start, end) => {
      try {
        // Parse object
        if (obj.slice(-1) === ',') {
          obj = obj.slice(0, -1); // Removes last ',' from object
        }
        let jsonObject = JSON.parse(obj);

        // Check if it is a new sender
        if (currentSenderId !== jsonObject.sender_id) {
          currentNumRows = 1;
          // Add new sender
          senders[jsonObject.sender_id] = { start };
        }
        senders[jsonObject.sender_id].numRows = currentNumRows;
        senders[jsonObject.sender_id].end = end;

        if (jsonObject.notification_send_date > lastDate) {
          lastDate = jsonObject.notification_send_date;
        }

        currentNumRows++;
        currentSenderId = jsonObject.sender_id;
      } catch (e) {
        if (!(e instanceof SyntaxError)) {
          console.error(e);
          console.error(obj);
        }
      }
    },
    objectVersionId
  );

  const end = performance.now();
  return {
    bucketName,
    bucketRegion: await s3Client.config.region(),
    objectKey,
    objectVersionId,
    genTimestamp,
    lastDate,
    objectProcessTimeMs: parseInt(end - start),
    objectSizeByte: bytes,
    sendersId: senders,
  };
};

module.exports = { createIndexObject };
