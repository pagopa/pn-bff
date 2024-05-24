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
  getPnIndexObjectKey,
} = require('./config.js');

const dlBucketName =
  getDlBucketName() === 'test' ? getPnBucketName() : getDlBucketName();
const dlBucketRegion =
  getDlBucketRegion() === 'test' ? getPnBucketRegion() : getDlBucketRegion();
const dlAssumeRoleArn = getDlAssumeRoleArn();
const dlOverviewObjectKey = getDlOverviewObjectKey();
const dlFocusObjectKey = getDlFocusObjectKey();

const pnBucketName = getPnBucketName();
const pnBucketRegion = getPnBucketRegion();
const pnIndexObjectKey = getPnIndexObjectKey();

const pnS3Client = new S3Client({ region: pnBucketRegion });

const dlCredentials =
  dlAssumeRoleArn !== 'test'
    ? getAssumeRoleCredentials(dlAssumeRoleArn, 'AssumeRoleSEND')
    : undefined;

const dlS3Client = new S3Client({
  region: dlBucketRegion,
  credentials: dlCredentials,
});

const handleEvent = async () => {
  const index = await createIndexObject(
    dlS3Client,
    dlBucketName,
    dlOverviewObjectKey,
    dlFocusObjectKey
  );

  await writeObject(
    pnS3Client,
    pnBucketName,
    pnIndexObjectKey,
    JSON.stringify(index)
  );

  return {
    statusCode: 200,
    body: 'OK',
  };
};

module.exports = { handleEvent };
