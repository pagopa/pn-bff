import { createIndexObject } from '../app/indexer.js';
import { expect } from 'chai';
import { sdkStreamMixin } from '@smithy/util-stream';
import { mockClient } from 'aws-sdk-client-mock';
import fs from 'node:fs';

import { S3Client, GetObjectCommand } from '@aws-sdk/client-s3';

describe('indexer tests', function () {
  it('s3StreamLines', async () => {
    // Given
    const s3Mock = mockClient(S3Client);
    const stream = fs.createReadStream('./src/test/data/overview.json');
    const sdkStream = sdkStreamMixin(stream);
    s3Mock.on(GetObjectCommand).resolves({
      Body: sdkStream,
      $metadata: { httpHeaders: { 'x-amz-version-id': 'testVersionId' } },
    });

    const s3 = new S3Client({ region: 'eu-south-1' });

    // When
    const index = await createIndexObject(
      s3,
      'testBucket',
      'testKey',
    );
    console.log(index);

    // Then
    expect(index.bucketName).to.be.equal('testBucket');
    expect(index.bucketRegion).to.be.equal('eu-south-1');
    expect(index.objectKey).to.be.equal('testKey');
    expect(index.objectVersionId).to.be.equal('testVersionId');
    expect(index.lastNotificationDate).to.be.equal('2024-04-17');
    expect(index.objectSizeByte).to.be.equal(1142348);
    expect(Object.keys(index.sendersId).length).to.be.equal(15);

    s3Mock.reset();
  });
});
