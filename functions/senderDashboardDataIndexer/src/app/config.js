/* istanbul ignore file */

const DL_BUCKET_REGION_DEFAULT = 'eu-central-1';
const PN_BUCKET_REGION_DEFAULT = 'eu-south-1';

/**
 * Retrieves the value of an environment variable.
 *
 * @param {string} env - The name of the environment variable to retrieve.
 * @param {any} [defaultValue] - The default value to return if the
 * environment variable is not defined.
 * @return {string} The value of the specified environment variable or the
 * default value if not defined.
 */
const getEnvironmentVariable = (env, defaultValue = undefined) => {
  const value = process.env[env];
  if (value === undefined) {
    return defaultValue;
  }
  return value;
};

export const getDlBucketName = () => getEnvironmentVariable('DL_BUCKET_NAME');

export const getDlBucketRegion = () =>
  getEnvironmentVariable('DL_BUCKET_REGION', DL_BUCKET_REGION_DEFAULT);

export const getDlAssumeRoleArn = () =>
  getEnvironmentVariable('DL_ASSUME_ROLE_ARN');

export const getDlOverviewObjectKey = () =>
  getEnvironmentVariable('DL_OVERVIEW_OBJECT_KEY');

export const getDlFocusObjectKey = () =>
  getEnvironmentVariable('DL_FOCUS_OBJECT_KEY');

export const getPnBucketName = () => getEnvironmentVariable('PN_BUCKET_NAME');

export const getPnBucketRegion = () =>
  getEnvironmentVariable('PN_BUCKET_REGION', PN_BUCKET_REGION_DEFAULT);

export const getPnOverviewIndexObjectKey = () =>
  getEnvironmentVariable('PN_OVERVIEW_INDEX_OBJECT_KEY');

export const getPnFocusIndexObjectKey = () =>
  getEnvironmentVariable('PN_FOCUS_INDEX_OBJECT_KEY');
