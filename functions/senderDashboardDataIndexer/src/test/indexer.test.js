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

    const overviewStream = fs.createReadStream('./src/test/data/overviewTest.json');
    const overviewSdkStream = sdkStreamMixin(overviewStream);

    const focusStream = fs.createReadStream('./src/test/data/focusTest.json');
    const focusSdkStream = sdkStreamMixin(focusStream);

    s3Mock.on(GetObjectCommand).callsFake((command) => {
      if (command.Key === 'testKeyOverview') {
        return {
          Body: overviewSdkStream,
        };
      } else if (command.Key === 'testKeyFocus') {
        return {
          Body: focusSdkStream,
        };
      }
    });

    s3Mock.on(HeadObjectCommand).resolves({
      VersionId: 'testVersionId',
    });

    const s3 = new S3Client({ region: 'eu-south-1' });

    // When
    const index = await createIndexObject(
      s3,
      'testBucket',
      'testKeyOverview',
      'testKeyFocus'
    );
    console.log(index);
    /* fs.writeFileSync(
      './src/test/data/index.json',
      JSON.stringify(index, null, 2)
    ); */

    // Then
    expect(index.bucketName).to.be.equal('testBucket');
    expect(index.bucketRegion).to.be.equal('eu-south-1');
    expect(index.overviewObjectKey).to.be.equal('testKeyOverview');
    expect(index.focusObjectKey).to.be.equal('testKeyFocus');
    expect(index.overviewObjectVersionId).to.be.equal('testVersionId');
    expect(index.focusObjectVersionId).to.be.equal('testVersionId');
    expect(index.lastDate).to.be.equal('2024-05-08');
    expect(index.startDate).to.be.equal('2023-07-17');
    expect(index.overviewObjectSizeByte).to.be.equal(1618696);
    expect(index.focusObjectSizeByte).to.be.equal(98669);
    expect(index.overviewObjectLines).to.be.equal(3356);
    expect(index.focusObjectLines).to.be.equal(579);
    expect(Object.keys(index.overviewSendersId).length).to.be.equal(12);
    expect(Object.keys(index.focusSendersId).length).to.be.equal(12);

    s3Mock.reset();
  });
});
