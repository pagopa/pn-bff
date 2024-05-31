const { S3Client, ListObjectVersionsCommand, CopyObjectCommand, PutObjectCommand } = require('@aws-sdk/client-s3');

const client = new S3Client({
  region: process.env.AWS_REGION,
  endpoint: process.env.AWS_ENDPOINT_URL,
  forcePathStyle: true
});

const bffBucketName = process.env.BFF_BUCKET_NAME;
const bffBucketPrefix = process.env.BFF_BUCKET_PREFIX;

const getLatestVersion = async (bffBucketName, fileName) => {
  try {
    console.log(`Listing object versions for bucket: ${bffBucketName}, file: ${fileName}`);
    const command = new ListObjectVersionsCommand({ Bucket: bffBucketName });
    const response = await client.send(command);

    if (!response.Versions || response.Versions.length === 0) {
      console.log('No versions found');
      return null;
    }

    const versions = response.Versions.filter(version => version.Key === fileName);
    if (versions.length === 0) {
      console.log('No matching versions found');
      return null;
    }

    const latestVersion = versions.reduce((latest, current) => {
      return current.LastModified > latest.LastModified ? current : latest;
    }, versions[0]);

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

  if(!toWebLandingBucket){
      const s3Key = `${bffBucketPrefix}/${fileName}_${configVersion}.csv`;
  } else {
      //TODO: Implement logic for NAME of file in web landing bucket
      const s3Key = 'TODO';
  }

  console.log(`Generated S3 key: ${s3Key}`);
  return s3Key;
}

module.exports = { getLatestVersion, generateS3Key};
