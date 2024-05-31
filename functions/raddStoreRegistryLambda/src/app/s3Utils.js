const { S3Client, ListObjectVersionsCommand, CopyObjectCommand, PutObjectCommand } = require('@aws-sdk/client-s3');

const client = new S3Client({
  region: process.env.AWS_REGION,
  endpoint: process.env.AWS_ENDPOINT_URL,
  forcePathStyle: true
});

const bffBucketName = process.env.BFF_BUCKET_NAME;
const bffBucketPrefix = process.env.BFF_BUCKET_PREFIX;
cons  webLandingBucketName = process.env.WEB_LANDING_BUCKET_NAME;
const webLandingBucketPrefix = process.env.WEB_LANDING_BUCKET_PREFIX;

const getLatestVersion = async (bffBucketName, bffBucketS3Key) => {
  try {
    console.log(`Listing object versions for bucket: ${bffBucketName}, file: ${bffBucketS3Key}`);
    const command = new ListObjectVersionsCommand({ Bucket: bffBucketName, Prefix: bffBucketS3Key, MaxKeys: 1});
    const response = await client.send(command);

    if (!response.Versions || response.Versions.length === 0) {
      console.log('No versions found');
      return null;
    }
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
    const s3Key = `${webLandingBucketPrefix}/${fileName}.csv`;
  }

  console.log(`Generated S3 key: ${s3Key}`);
  return s3Key;
}

const uploadFile = async (bffBucketName, key, fileContent) => {
  try {
    console.log(`Uploading file to bucket: ${bffBucketName}, key: ${key}`);
    const command = new PutObjectCommand({
      Bucket: bffBucketName,
      Key: key,
      Body: fileContent
    });

    const response = await client.send(command);
    console.log('Successfully uploaded object:', response);
  } catch (error) {
    console.error('Error uploading object:', error);
    throw error;
  }
};

async function uploadVersionedFile(sendToWebLanding, bffBucketS3Key, csvContent, configurationVersion) {
    await uploadFile(bffBucketName, bffBucketS3Key, csvContent);
    console.log('File uploaded to S3:', bffBucketS3Key);

    if (sendToWebLanding)) {
      const webLandingS3Key = generateS3Key(null, true);
      await copyObject(bffBucketName, bffBucketS3Key, webLandingBucketName, webLandingS3Key);
      console.log('File copied to site bucket:', webLandingS3Key);
    }
}

module.exports = { getLatestVersion, generateS3Key, uploadVersionedFile };
