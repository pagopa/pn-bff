const chai = require('chai');
const chaiAsPromised = require('chai-as-promised');
const { mockClient } = require('aws-sdk-client-mock');
const {
  S3Client,
  ListObjectVersionsCommand,
  PutObjectCommand,
  CopyObjectCommand,
} = require('@aws-sdk/client-s3');
const {
  getLatestVersion,
  generateS3Key,
  uploadVersionedFile,
} = require('../app/s3Utils');

chai.use(chaiAsPromised);
const expect = chai.expect;

describe('s3Utils tests', function () {
  let s3Mock;

  beforeEach(() => {
    s3Mock = mockClient(S3Client);
  });

  afterEach(() => {
    s3Mock.reset();
  });

  describe('getLatestVersion', function () {
    it('should return the latest version', async () => {
      // Given
      const bffBucketS3Key = 'testKey';
      const latestVersion = [
        { Key: 'file1.txt', VersionId: '1', IsLatest: true },
      ];

      s3Mock
        .on(ListObjectVersionsCommand)
        .resolves({ Versions: latestVersion });

      // When
      const result = await getLatestVersion(bffBucketS3Key);

      // Then
      expect(result.VersionId).to.be.equal('1');
      expect(result.Key).to.be.equal('file1.txt');
      expect(result.IsLatest).to.be.equal(true);
    });

    it('should return null if no versions found', async () => {
      // Given
      const bffBucketS3Key = 'testKey';
      s3Mock.on(ListObjectVersionsCommand).resolves({
        Versions: [],
      });

      // When
      const result = await getLatestVersion(bffBucketS3Key);

      // Then
      expect(result).to.be.null;
    });

    it('should throw an error if listing object versions fails', async () => {
      // Given
      const bffBucketS3Key = 'testKey';
      const errorMessage = 'Failed to list object versions';
      s3Mock.on(ListObjectVersionsCommand).rejects(new Error(errorMessage));

      // When/Then
      await expect(getLatestVersion(bffBucketS3Key)).to.be.rejectedWith(
        errorMessage
      );
    });
  });

  describe('generateS3Key', function () {
    it('should generate S3 key for bffBucket', () => {
      // Given
      const configVersion = 'testVersion';
      const toWebLandingBucket = false;
      process.env.FILE_NAME = 'testFile';
      process.env.BFF_BUCKET_PREFIX = 'testPrefix';
      const expectedS3Key = 'testPrefix/testFile_testVersion.csv';

      // When
      const result = generateS3Key(configVersion, toWebLandingBucket);

      // Then
      expect(result).to.equal(expectedS3Key);
    });

    it('should generate S3 key for webLandingBucket', () => {
      // Given
      const toWebLandingBucket = true;
      process.env.FILE_NAME = 'testFile';
      process.env.WEB_LANDING_BUCKET_PREFIX = 'testWebLandingPrefix';
      const expectedS3Key = 'testWebLandingPrefix/testFile.csv';

      // When
      const result = generateS3Key(null, toWebLandingBucket);

      // Then
      expect(result).to.equal(expectedS3Key);
    });

    it('should throw an error if FILE_NAME environment variable is missing', () => {
      // Given
      delete process.env.FILE_NAME;

      // When/Then
      expect(() => generateS3Key('testVersion', false)).to.throw(
        'FILE_NAME environment variable is missing'
      );
    });
  });

  describe('uploadVersionedFile', function () {
    it('should upload file to bffBucket and copy to webLandingBucket', async () => {
      // Given
      process.env.BFF_BUCKET_NAME = 'bffBucket';
      process.env.WEB_LANDING_BUCKET_NAME = 'webLandingBucket';
      process.env.FILE_NAME = 'testFile';
      process.env.WEB_LANDING_BUCKET_PREFIX = 'testWebLandingPrefix';
      const sendToWebLanding = true;
      const bffBucketS3Key = 'testKey';
      const csvContent = 'testContent';
      s3Mock.on(PutObjectCommand).resolves({});
      s3Mock.on(CopyObjectCommand).resolves({});

      // When
      await uploadVersionedFile(sendToWebLanding, bffBucketS3Key, csvContent);

      // Then
      expect(s3Mock.calls().length).to.equal(2);
      expect(s3Mock.calls(PutObjectCommand)[0].args[0].input).to.deep.equal({
        Bucket: 'bffBucket',
        Key: 'testKey',
        Body: 'testContent',
      });
      expect(s3Mock.calls(CopyObjectCommand)[1].args[0].input).to.deep.equal({
        Bucket: 'webLandingBucket',
        CopySource: 'bffBucket/testKey',
        Key: 'testWebLandingPrefix/testFile.csv',
      });
    });

    it('should upload file to bffBucket without copying to webLandingBucket', async () => {
      // Given
      const sendToWebLanding = false;
      const bffBucketS3Key = 'testKey';
      const csvContent = 'testContent';
      process.env.BFF_BUCKET_NAME = 'bffBucket';

      s3Mock.on(PutObjectCommand).resolves({});

      // When
      await uploadVersionedFile(sendToWebLanding, bffBucketS3Key, csvContent);

      // Then
      expect(s3Mock.calls().length).to.equal(1);
      expect(s3Mock.calls()[0].args[0].input).to.deep.equal({
        Bucket: 'bffBucket',
        Key: 'testKey',
        Body: 'testContent',
      });
    });

    it('should upload file to bffBucket with error on upload', async () => {
      // Given
      const sendToWebLanding = false;
      const bffBucketS3Key = 'testKey';
      const csvContent = 'testContent';
      process.env.BFF_BUCKET_NAME = 'bffBucket';
      const errorMessage = 'Error uploading object';

      s3Mock.on(PutObjectCommand).rejects(new Error(errorMessage));

      // Then
      await expect(
        uploadVersionedFile(sendToWebLanding, bffBucketS3Key, csvContent)
      ).to.be.rejectedWith(errorMessage);
    });

    it('should upload file to bffBucket and copy to webLandingBucket with error on copy', async () => {
      // Given
      process.env.BFF_BUCKET_NAME = 'bffBucket';
      process.env.WEB_LANDING_BUCKET_NAME = 'webLandingBucket';
      process.env.FILE_NAME = 'testFile';
      process.env.WEB_LANDING_BUCKET_PREFIX = 'testWebLandingPrefix';
      const sendToWebLanding = true;
      const bffBucketS3Key = 'testKey';
      const csvContent = 'testContent';

      const errorMessage = 'Error copy object';

      s3Mock.on(PutObjectCommand).resolves({});
      s3Mock.on(CopyObjectCommand).rejects(new Error(errorMessage));

      await expect(
        uploadVersionedFile(sendToWebLanding, bffBucketS3Key, csvContent)
      ).to.be.rejectedWith(errorMessage);
    });
  });
});
