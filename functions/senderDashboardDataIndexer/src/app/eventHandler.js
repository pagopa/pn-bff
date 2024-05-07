import { S3Client } from '@aws-sdk/client-s3';
import { getAssumeRoleCredentials } from './sts.js';
import { writeObject } from './s3.js';
import { createIndexObject } from './indexer.js';
import {
  getDlBucketName,
  getDlBucketRegion,
  getDlAssumeRoleArn,
  getDlOverviewObjectKey,
  getDlFocusObjectKey,
  getPnBucketName,
  getPnBucketRegion,
  getPnOverviewIndexObjectKey,
  getPnFocusIndexObjectKey,
} from './config.js';

const dlBucketName = getDlBucketName();
const dlBucketRegion = getDlBucketRegion();
const dlAssumeRoleArn = getDlAssumeRoleArn();
const dlOverviewObjectKey = getDlOverviewObjectKey();
const dlFocusObjectKey = getDlFocusObjectKey();

const pnBucketName = getPnBucketName();
const pnBucketRegion = getPnBucketRegion();
const pnOverviewIndexObjectKey = getPnOverviewIndexObjectKey();
const pnFocusIndexObjectKey = getPnFocusIndexObjectKey();

const dlCredentials = getAssumeRoleCredentials(
  dlAssumeRoleArn,
  'AssumeRoleSEND'
);
const dlS3Client = new S3Client({
  region: dlBucketRegion,
  credentials: dlCredentials,
});
const pnS3Client = new S3Client({ region: pnBucketRegion });

export const handleEvent = async () => {
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
    body: 'Ok',
  };
};
