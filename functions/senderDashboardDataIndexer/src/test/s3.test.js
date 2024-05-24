const { streamLines, writeObject, headObject } = require('../app/s3.js');
const chai = require('chai');
const chaiAsPromised = require('chai-as-promised');
const { sdkStreamMixin } = require('@smithy/util-stream');
const { mockClient } = require('aws-sdk-client-mock');
const fs = require('node:fs');
const { PassThrough } = require('stream');
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
    const stream = fs.createReadStream('./src/test/data/overviewTest.json');
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
    expect(lines).to.be.equal(3356);
    expect(bytes).to.be.equal(1618696);
    s3Mock.reset();
  });

  it('should handle stream error in streamLines', async () => {
    // Given
    const s3Mock = mockClient(S3Client);
    const errorStream = new require('stream').Readable();
    s3Mock.on(GetObjectCommand).resolves({ Body: errorStream });
    const s3 = new S3Client({});

    // When
    errorStream._read = () => {
      errorStream.emit('error', new Error('Stream error'));
    };

    // Then
    await expect(
      streamLines(s3, 'test', 'test', () => {})
    ).to.eventually.be.rejectedWith('Stream error');
    s3Mock.reset();
  });

  describe('streamLines', () => {
    it('correctly processes lines split across chunks', async () => {
      // Given
      const stream = new PassThrough();
      const results = [];

      // When
      const processLines = streamLines(
        { send: () => ({ Body: stream }) },
        'bucket',
        'key',
        (line, start, end) => {
          results.push(line);
        }
      );

      stream.write('Hello, Wo');
      stream.write('rld!\nThis');
      stream.write(' is a test.\nAnother line');
      stream.end();

      // Wait for the promise from streamLines to resolve
      const { lines, bytes } = await processLines;

      // Then
      expect(results).to.deep.equal([
        'Hello, World!',
        'This is a test.',
        'Another line',
      ]);
      expect(lines).to.equal(3);
      expect(bytes).to.be.greaterThan(0);
    });
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
