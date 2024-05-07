import { streamLines, writeObject } from '../app/s3.js';
import { expect } from 'chai';
import { sdkStreamMixin } from '@smithy/util-stream';
import { mockClient } from 'aws-sdk-client-mock';
import fs from 'node:fs';

import {
  S3Client,
  GetObjectCommand,
  PutObjectCommand,
} from '@aws-sdk/client-s3';

describe('s3 tests', function () {
  it('streamLines', async () => {
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

  it('writeObject', async () => {
    // Given
    const s3Mock = mockClient(S3Client);
    s3Mock.on(PutObjectCommand).resolves({});

    const s3 = new S3Client({});

    // When - Then
    await writeObject(s3, 'test', 'test', 'test');

    s3Mock.reset();
  });
});
