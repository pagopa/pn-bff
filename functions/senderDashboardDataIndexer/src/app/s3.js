const {
  GetObjectCommand,
  PutObjectCommand,
  HeadObjectCommand,
} = require('@aws-sdk/client-s3');

/**
 * Retrieves the metadata for an object in an S3 bucket using the HEAD operation.
 *
 * @param {S3Client} s3Client The AWS S3 client instance.
 * @param {string} bucketName The name of the S3 bucket.
 * @param {string} objectKey The key of the object within the S3 bucket.
 * @returns {Promise<object>} A promise that resolves to the metadata of the object.
 */
const headObject = async (s3Client, bucketName, objectKey) => {
  const params = new HeadObjectCommand({
    Bucket: bucketName,
    Key: objectKey,
  });

  console.log(`Doing HEAD: s3://${bucketName}/${objectKey}`);
  return await s3Client.send(params);
};

/**
 * Processes each line of a file stored in S3, invoking a callback function with
 * the line and its byte range.
 *
 * @param {S3Client} s3Client The AWS S3 client instance.
 * @param {string} bucketName The name of the S3 bucket.
 * @param {string} objectKey The key of the object within the S3 bucket.
 * @param {function(string, number, number): void} lineCallback A callback
 * function that receives each line and its byte range (startByte, endByte).
 * @param {string} [versionId] Optional version ID of the object to fetch a
 * specific version.
 * @returns {Promise<{lines: number, bytes: number}>} A promise that resolves
 * with the number of lines read and total bytes processed.
 */
const streamLines = async (
  s3Client,
  bucketName,
  objectKey,
  lineCallback,
  versionId
) => {
  const params = {
    Bucket: bucketName,
    Key: objectKey,
    ...(versionId && { VersionId: versionId }), // Add VersionId if provided
  };

  console.log(
    `Doing GET: s3://${bucketName}/${objectKey}${
      versionId ? `?versionId=${versionId}` : ''
    }`
  );
  const data = await s3Client.send(new GetObjectCommand(params));
  const stream = data.Body;

  let remainder = '';
  let startByte = 0;
  let endByte = -1;

  return await new Promise((resolve, reject) => {
    let numLines = 0;

    stream.on('data', (chunk) => {
      const chunkAsString = remainder + chunk.toString();
      const lines = chunkAsString.split('\n');
      remainder = lines.pop();

      lines.forEach((line) => {
        endByte += line.length + 1; // Adds 1 for the newline character
        lineCallback(line, startByte, endByte);
        numLines++;
        startByte = endByte + 1;
      });
    });

    stream.on('end', () => {
      /* istanbul ignore next */
      if (remainder) {
        endByte = startByte + remainder.length - 1;
        lineCallback(remainder, startByte, endByte);
        numLines++;
      }
      resolve({ lines: numLines, bytes: endByte + 1 });
    });

    stream.on('error', reject);
  });
};

/**
 * Read an Object from S3 bucket.
 *
 * @param {S3Client} s3Client The AWS S3 client instance.
 * @param {string} bucketName The name of the S3 bucket.
 * @param {string} objectKey The key of the object within the S3 bucket.
 * @param {any} body The body of the object.
 */
const writeObject = async (s3Client, bucketName, objectKey, body) => {
  const params = {
    Bucket: bucketName,
    Key: objectKey,
    Body: body,
  };
  const command = new PutObjectCommand(params);
  console.log(`Doing PUT: s3://${bucketName}/${objectKey}`);
  await s3Client.send(command);
};

module.exports = { headObject, streamLines, writeObject };
