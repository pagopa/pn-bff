const { streamLines, writeObject, headObject } = require('../app/s3.js');
const chai = require('chai');
const chaiAsPromised = require('chai-as-promised');
const { sdkStreamMixin } = require('@smithy/util-stream');
const { mockClient } = require('aws-sdk-client-mock');
const fs = require('node:fs');
const {
  S3Client,
  GetObjectCommand,
  PutObjectCommand,
  HeadObjectCommand,
} = require('@aws-sdk/client-s3');

chai.use(chaiAsPromised);
const expect = chai.expect;

describe('s3 tests', function () {
  it('should streamLines', async () => {
    // Given
    const s3Mock = mockClient(S3Client);
    const stream = fs.createReadStream('./src/test/data/overview.json');
    const sdkStream = sdkStreamMixin(stream);
    s3Mock.on(GetObjectCommand).resolves({
      Body: sdkStream,
    });

    const s3 = new S3Client({});
    let lastEnd = -1;

    // When
    const { lines, bytes } = await streamLines(
      s3,
      'test',
      'test',
      (obj, start, end) => {
        // Then
        expect(start).to.be.equal(lastEnd + 1);
        lastEnd = end;
      }
    );

    // Then
    expect(lines).to.be.equal(2369);
    expect(bytes).to.be.equal(1142348);
    s3Mock.reset();
  });

  it('should handle stream error in streamLines', async () => {
    const s3Mock = mockClient(S3Client);
    const errorStream = new require('stream').Readable();
    errorStream._read = () => {
      errorStream.emit('error', new Error('Stream error'));
    };

    s3Mock.on(GetObjectCommand).resolves({ Body: errorStream });

    const s3 = new S3Client({});
    await expect(
      streamLines(s3, 'test', 'test', () => {})
    ).to.eventually.be.rejectedWith('Stream error');
    s3Mock.reset();
  });

  it('should writeObject', async () => {
    // Given
    const s3Mock = mockClient(S3Client);
    s3Mock.on(PutObjectCommand).resolves({});

    const s3 = new S3Client({});

    // When - Then
    await writeObject(s3, 'test', 'test', 'test');

    s3Mock.reset();
  });

  it('should headObject', async () => {
    // Given
    const s3Mock = mockClient(S3Client);
    s3Mock.on(HeadObjectCommand).resolves({ VersionId: 'test' });

    const s3 = new S3Client({});

    // When
    const metadata = await headObject(s3, 'test', 'test');

    // Then
    expect(metadata.VersionId).to.be.equal('test');

    s3Mock.reset();
  });
});
