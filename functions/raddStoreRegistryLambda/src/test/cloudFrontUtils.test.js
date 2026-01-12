const chai = require('chai');
const chaiAsPromised = require('chai-as-promised');
const { mockClient } = require('aws-sdk-client-mock');
const {
  CloudFrontClient,
  CreateInvalidationCommand,
} = require('@aws-sdk/client-cloudfront');
const { invalidateCache } = require('../app/cloudFrontUtils');

chai.use(chaiAsPromised);
const expect = chai.expect;

describe('cloudFrontUtils tests', function () {
  let cloudFrontMock;

  beforeEach(() => {
    cloudFrontMock = mockClient(CloudFrontClient);
  });

  afterEach(() => {
    cloudFrontMock.reset();
  });

  describe('invalidateCache', function () {
    it('should invalidate cache', async () => {
      // Given
      cloudFrontMock.on(CreateInvalidationCommand).resolves({});

      // When
      await invalidateCache('testWebLandingDistributionId', [
        'testWebLandingPrefix/testFile.csv',
      ]);

      // Then
      expect(cloudFrontMock.calls().length).to.equal(1);
      const cloudFrontInvalidationInput = cloudFrontMock.calls(
        CreateInvalidationCommand
      )[0].args[0].input;

      expect(cloudFrontInvalidationInput)
        .to.be.an('object')
        .and.have.keys('DistributionId', 'InvalidationBatch');
      expect(cloudFrontInvalidationInput.InvalidationBatch)
        .to.be.an('object')
        .and.have.keys('CallerReference', 'Paths');
      expect(
        cloudFrontInvalidationInput.InvalidationBatch.CallerReference
      ).to.be.an('string');
      expect(cloudFrontInvalidationInput.InvalidationBatch.Paths)
        .to.be.an('object')
        .and.have.keys('Items', 'Quantity');
      expect(
        cloudFrontInvalidationInput.InvalidationBatch.Paths.Quantity
      ).to.equal(1);
      expect(cloudFrontInvalidationInput.InvalidationBatch.Paths.Items)
        .to.be.an('array')
        .and.length(1)
        .and.have.all.members(['testWebLandingPrefix/testFile.csv']);
    });

    it('should not invalidate cache due to error', async () => {
      // Given
      const errorMessage = 'Error invalidating cache';

      cloudFrontMock
        .on(CreateInvalidationCommand)
        .rejects(new Error(errorMessage));

      // Then
      await expect(
        invalidateCache('testWebLandingDistributionId', [
          'testWebLandingPrefix/testFile.csv',
        ])
      ).to.be.rejectedWith(errorMessage);
    });
  });
});
