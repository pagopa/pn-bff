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
  getNAlarmDays,
} = require('./config.js');

const dlBucketName = getDlBucketName();
const dlBucketRegion = getDlBucketRegion();
const dlAssumeRoleArn = getDlAssumeRoleArn();
const dlOverviewObjectKey = getDlOverviewObjectKey();
const dlFocusObjectKey = getDlFocusObjectKey();

const pnBucketName = getPnBucketName();
const pnBucketRegion = getPnBucketRegion();
const pnIndexObjectKey = getPnIndexObjectKey();
const pnNAlarmDays = getNAlarmDays();

const pnS3Client = new S3Client({ region: pnBucketRegion });

const dlCredentials = getAssumeRoleCredentials(
  dlAssumeRoleArn,
  'AssumeRoleSEND'
);

const dlS3Client = new S3Client({
  region: dlBucketRegion,
  credentials: dlCredentials,
});

/**
 * Check if the last data date is older than the alarm days
 * @param index - Index object
 * @param pnNAlarmDays - Number of alarm days
 */
const checkLastDataDate = (index, pnNAlarmDays) => {
  const referenceDate = new Date();
  const lastDate = new Date(index.lastDate);
  const nAlarmDays = parseInt(pnNAlarmDays, 10);

  referenceDate.setDate(referenceDate.getDate() - nAlarmDays);

  if (lastDate < referenceDate) {
    console.error(
      `No data in the last ${nAlarmDays} days. Last data date: ${index.lastDate}`
    );
  }
};

const handleEvent = async () => {
  const index = await createIndexObject(
    dlS3Client,
    dlBucketName,
    dlOverviewObjectKey,
    dlFocusObjectKey
  );

  checkLastDataDate(index, pnNAlarmDays);

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

module.exports = { handleEvent, checkLastDataDate };
