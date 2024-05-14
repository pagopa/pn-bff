const { S3Client } = require('@aws-sdk/client-s3');
const { getAssumeRoleCredentials } = require('./sts.js');
const { writeObject } = require('./s3.js');
const { createIndexObject } = require('./indexer.js');
const {
  getDlBucketName,
  getDlBucketRegion,
  getDlAssumeRoleArn,
  getDlOverviewObjectKey,
  getDlFocusObjectKey,
  getPnBucketName,
  getPnBucketRegion,
  getPnOverviewIndexObjectKey,
  getPnFocusIndexObjectKey,
} = require('./config.js');

const dlBucketName = getDlBucketName();
const dlBucketRegion = getDlBucketRegion();
const dlAssumeRoleArn = getDlAssumeRoleArn();
const dlOverviewObjectKey = getDlOverviewObjectKey();
const dlFocusObjectKey = getDlFocusObjectKey();

const pnBucketName = getPnBucketName();
const pnBucketRegion = getPnBucketRegion();
const pnOverviewIndexObjectKey = getPnOverviewIndexObjectKey();
const pnFocusIndexObjectKey = getPnFocusIndexObjectKey();

const pnS3Client = new S3Client({ region: pnBucketRegion });

const dlCredentials =
  dlAssumeRoleArn !== 'test'
    ? getAssumeRoleCredentials(dlAssumeRoleArn, 'AssumeRoleSEND')
    : undefined;

const dlS3Client =
  dlBucketName !== 'test'
    ? new S3Client({
        region: dlBucketRegion,
        credentials: dlCredentials,
      })
    : undefined;

const handleEvent = async () => {
  const overviewIndex = await createIndexObject(
    dlS3Client,
    dlBucketName,
    dlOverviewObjectKey
  );
  const focusIndex = await createIndexObject(
    dlS3Client,
    dlBucketName,
    dlFocusObjectKey
  );

  await writeObject(
    pnS3Client,
    pnBucketName,
    pnOverviewIndexObjectKey,
    JSON.stringify(overviewIndex)
  );

  await writeObject(
    pnS3Client,
    pnBucketName,
    pnFocusIndexObjectKey,
    JSON.stringify(focusIndex)
  );

  return {
    statusCode: 200,
    body: 'OK',
  };
};

module.exports = { handleEvent };
