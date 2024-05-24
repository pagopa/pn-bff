const { streamLines, headObject } = require('./s3.js');
const { performance } = require('perf_hooks');

/**
 * Creates a map of senders with their respective byte ranges in the src object.
 *
 * @param {Object} s3Client - The initialized S3 client.
 * @param {string} bucketName - The name of the S3 bucket.
 * @param {string} objectKey - The key of the S3 object to stream lines from.
 * @param {string} objectVersionId - The version ID of the S3 object.
 * @returns {Promise<Object>} A promise that resolves to an object containing
 * sender details, start and end dates, object size, and number of lines.
 */
const createSenderMap = async (
  s3Client,
  bucketName,
  objectKey,
  objectVersionId
) => {
  const senders = {};
  let lastDate;
  let startDate;
  let currentNumRows = 1;
  let currentSenderId;

  // Objects are ordered by sender_id
  const { lines, bytes } = await streamLines(
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

        if (!lastDate || jsonObject.notification_send_date > lastDate) {
          lastDate = jsonObject.notification_send_date;
        }
        if (!startDate || jsonObject.notification_send_date < startDate) {
          startDate = jsonObject.notification_send_date;
        }

        currentNumRows++;
        currentSenderId = jsonObject.sender_id;
      } catch (e) {
        /* istanbul ignore next */
        if (!(e instanceof SyntaxError)) {
          console.error(e);
          console.error(obj);
        }
      }
    },
    objectVersionId
  );

  return {
    startDate,
    lastDate,
    senders,
    objectSizeByte: bytes,
    objectLines: lines,
  };
};

/**
 * Creates an index object containing the bytes range for each sender sender_id.
 *
 * @param {Object} s3Client The initialized S3 client.
 * @param {string} bucketName The name of the S3 bucket.
 * @param {string} overviewObjectKey The key of the overview S3 object.
 * @param {string} focusObjectKey The key of the focus S3 object.
 * specific version.
 * @returns {Promise<Object>} A promise that resolves to an object containing
 * metadata about the process and sender details.
 */
const createIndexObject = async (
  s3Client,
  bucketName,
  overviewObjectKey,
  focusObjectKey
) => {
  // Indexing overview
  console.log(`Indexing: s3://${bucketName}/${overviewObjectKey}`);
  let start = performance.now();
  // Get object metadata
  let metadata = await headObject(s3Client, bucketName, overviewObjectKey);
  console.log(metadata);
  overviewObjectVersionId = metadata.VersionId;
  // Create index
  const resOverview = await createSenderMap(
    s3Client,
    bucketName,
    overviewObjectKey,
    overviewObjectVersionId
  );
  const overviewObjectProcessTimeMs = parseInt(performance.now() - start);

  // Indexing focus
  console.log(`Indexing: s3://${bucketName}/${focusObjectKey}`);
  start = performance.now();
  // Get object metadata
  metadata = await headObject(s3Client, bucketName, focusObjectKey);
  console.log(metadata);
  focusObjectVersionId = metadata.VersionId;

  const resFocus = await createSenderMap(
    s3Client,
    bucketName,
    focusObjectKey,
    focusObjectVersionId
  );
  const focusObjectProcessTimeMs = parseInt(performance.now() - start);

  return {
    bucketName,
    bucketRegion: await s3Client.config.region(),
    overviewObjectKey,
    focusObjectKey,
    overviewObjectVersionId,
    focusObjectVersionId,
    genTimestamp: new Date().toISOString(),
    startDate: resOverview.startDate,
    lastDate: resOverview.lastDate,
    overviewObjectProcessTimeMs,
    focusObjectProcessTimeMs,
    overviewObjectSizeByte: resOverview.objectSizeByte,
    focusObjectSizeByte: resFocus.objectSizeByte,
    overviewObjectLines: resOverview.objectLines,
    focusObjectLines: resFocus.objectLines,
    overviewSendersId: resOverview.senders,
    focusSendersId: resFocus.senders,
  };
};

module.exports = { createIndexObject };
