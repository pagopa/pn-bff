// Mock the environment variables
function setupEnv() {
  process.env = {
    BFF_BUCKET_NAME: 'radd-store-locator',
    BFF_BUCKET_PREFIX: 'pn-store-locator',
    WEB_LANDING_BUCKET_NAME: 'web-landing-store-locator',
    WEB_LANDING_BUCKET_PREFIX: 'web-landing',
    FILE_NAME: 'test-csv-file',
    CSV_CONFIGURATION_PARAMETER:
      '/pn-radd-store-registry-lambda/csv-configuration',
    RADD_STORE_GENERATION_CONFIG_PARAMETER:
      '/pn-radd-store-registry-lambda/send-to-web-landing',
    GENERATE_INTERVAL: '1',
    RADD_STORE_REGISTRY_API_URL:
      'http://0.0.0.0:3000/radd-net-private/api/v1/store',
    REGISTRY_PAGINATION_LIMIT: '1000',
    AWS_REGION: 'us-east-1',
    AWS_XRAY_CONTEXT_MISSING: 'IGNORE_ERROR',
    AWS_SECRET_ACCESS_KEY: 'TEST',
    AWS_ACCESS_KEY_ID: 'TEST',
    AWS_PROFILE_NAME: 'default',
    AWS_ENDPOINT_URL: 'http://localhost:4566/',
    AWS_LOCATION_REGION: 'eu-central-1',
    AWS_LOCATION_REQUESTS_PER_SECOND: '95',
    MALFORMED_ADDRESS_THRESHOLD: '0.7',
  };
}

module.exports = {
  setupEnv,
};
