const { createIndexObject } = require('../app/indexer.js');
const { expect } = require('chai');
const { sdkStreamMixin } = require('@smithy/util-stream');
const { mockClient } = require('aws-sdk-client-mock');
const fs = require('node:fs');

const {
  S3Client,
  GetObjectCommand,
  HeadObjectCommand,
} = require('@aws-sdk/client-s3');

describe('indexer tests', function () {
  it('should createIndexObject', async () => {
    // Given
    const s3Mock = mockClient(S3Client);
    const stream = fs.createReadStream('./src/test/data/overview.json');
    const sdkStream = sdkStreamMixin(stream);
    s3Mock.on(GetObjectCommand).resolves({
      Body: sdkStream,
      $metadata: { httpHeaders: { 'x-amz-version-id': 'testVersionId' } },
    });

    s3Mock.on(HeadObjectCommand).resolves({
      VersionId: 'testVersionId',
    });

    const s3 = new S3Client({ region: 'eu-south-1' });

    // When
    const index = await createIndexObject(s3, 'testBucket', 'testKey');
    console.log(index);

    // Then
    expect(index.bucketName).to.be.equal('testBucket');
    expect(index.bucketRegion).to.be.equal('eu-south-1');
    expect(index.objectKey).to.be.equal('testKey');
    expect(index.objectVersionId).to.be.equal('testVersionId');
    expect(index.lastDate).to.be.equal('2024-04-17');
    expect(index.objectSizeByte).to.be.equal(1142348);
    expect(Object.keys(index.sendersId).length).to.be.equal(14);

    s3Mock.reset();
  });
});
