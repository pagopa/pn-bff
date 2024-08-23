const { S3Client } = require('@aws-sdk/client-s3');
const { getAssumeRoleCredentials } = require('./sts.js');
const { writeObject } = require('./s3.js');
const { createIndexObject } = require('./indexer.js');
const { checkLastDataDate } = require('./checkLastDate.js');
const {
  getDlBucketName,
  getDlBucketRegion,
  getDlAssumeRoleArn,
  getDlOverviewObjectKey,
  getDlFocusObjectKey,
  getPnBucketName,
  getPnBucketRegion,
  getPnIndexObjectKey,
  getAlarmNDays,
} = require('./config.js');

const dlBucketName = getDlBucketName();
const dlBucketRegion = getDlBucketRegion();
const dlAssumeRoleArn = getDlAssumeRoleArn();
const dlOverviewObjectKey = getDlOverviewObjectKey();
const dlFocusObjectKey = getDlFocusObjectKey();

const pnBucketName = getPnBucketName();
const pnBucketRegion = getPnBucketRegion();
const pnIndexObjectKey = getPnIndexObjectKey();
const alarmNDays = getAlarmNDays();

const pnS3Client = new S3Client({ region: pnBucketRegion });

const dlCredentials = getAssumeRoleCredentials(
  dlAssumeRoleArn,
  'AssumeRoleSEND'
);

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

  checkLastDataDate(index, alarmNDays);

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
