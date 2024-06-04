const { S3Client, ListObjectVersionsCommand, CopyObjectCommand, PutObjectCommand } = require('@aws-sdk/client-s3');

const client = new S3Client({
  region: process.env.AWS_REGION,
  forcePathStyle: true
});

const getLatestVersion = async (bffBucketS3Key) => {
  try {
    const bffBucketName = process.env.BFF_BUCKET_NAME;
    console.log(`Listing object versions for bucket: ${bffBucketName}, file: ${bffBucketS3Key}`);
    const command = new ListObjectVersionsCommand({ Bucket: bffBucketName, Prefix: bffBucketS3Key, MaxKeys: 1});
    const response = await client.send(command);

    if (!response.Versions || response.Versions.length === 0) {
      console.log('No versions found');
      return null;
    }
    const latestVersion = response.Versions[0];
    console.log(`Latest version found: ${latestVersion.VersionId}`);
    return latestVersion;

  } catch (error) {
    console.error('Error listing object versions:', error);
    throw new Error('Failed to list object versions');
  }
};

function generateS3Key(configVersion, toWebLandingBucket) {
  const fileName = process.env.FILE_NAME;
  if (!fileName) {
    throw new Error('FILE_NAME environment variable is missing');
  }

  let s3Key;
  if(!toWebLandingBucket){
    const bffBucketPrefix = process.env.BFF_BUCKET_PREFIX;
    s3Key = `${bffBucketPrefix}/${fileName}_${configVersion}.csv`;
  } else {
      //TODO: Implement logic for NAME of file in web landing bucket
      s3Key = 'TODO';
  }

  console.log(`Generated S3 key: ${s3Key}`);
  return s3Key;
}

module.exports = { getLatestVersion, generateS3Key};
