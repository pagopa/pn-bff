const {
  CloudFrontClient,
  CreateInvalidationCommand,
} = require('@aws-sdk/client-cloudfront');

const client = new CloudFrontClient({ region: process.env.AWS_REGION });

async function invalidateCache(distributionId, paths) {
  // invalidate CloudFront cache
  try {
    const command = new CreateInvalidationCommand({
      DistributionId: distributionId,
      InvalidationBatch: {
        Paths: {
          Quantity: paths.length,
          Items: paths,
        },
        // an identifier to prevent from accidentally resubmitting an identical request
        CallerReference: Date.now().toString(),
      },
    });

    await client.send(command);
    console.log('CloudFront cache invalidated');
  } catch (error) {
    console.error('Error invalidating CloudFront cache:', error);
    throw error;
  }
}

module.exports = { invalidateCache };
