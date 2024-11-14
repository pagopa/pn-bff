/* istanbul ignore file */

const DL_BUCKET_REGION_DEFAULT = 'eu-central-1';
const PN_BUCKET_REGION_DEFAULT = 'eu-south-1';
const N_ROWS_TO_LOG = '10';
const DEFAULT_MIN_BYTES_DATA_LAKE_FILE = '1000000';

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

const getDlBucketName = () => getEnvironmentVariable('DL_BUCKET_NAME');

const getDlBucketRegion = () =>
  getEnvironmentVariable('DL_BUCKET_REGION', DL_BUCKET_REGION_DEFAULT);

const getDlAssumeRoleArn = () => getEnvironmentVariable('DL_ASSUME_ROLE_ARN');

const getDlOverviewObjectKey = () =>
  getEnvironmentVariable('DL_OVERVIEW_OBJECT_KEY');

const getDlFocusObjectKey = () => getEnvironmentVariable('DL_FOCUS_OBJECT_KEY');

const getPnBucketName = () => getEnvironmentVariable('PN_BUCKET_NAME');

const getPnBucketRegion = () =>
  getEnvironmentVariable('PN_BUCKET_REGION', PN_BUCKET_REGION_DEFAULT);

const getPnIndexObjectKey = () => getEnvironmentVariable('PN_INDEX_OBJECT_KEY');

const getAlarmNDays = () => Number(getEnvironmentVariable('ALARM_N_DAYS'));

const getMinBytesDataLakeFile = () =>
  Number(
    getEnvironmentVariable(
      'MIN_BYTES_DATA_LAKE_FILE',
      DEFAULT_MIN_BYTES_DATA_LAKE_FILE
    )
  );

const getRowsToLog = () =>
  Number(getEnvironmentVariable('ROWS_TO_LOG', N_ROWS_TO_LOG));

module.exports = {
  getDlBucketName,
  getDlBucketRegion,
  getDlAssumeRoleArn,
  getDlOverviewObjectKey,
  getDlFocusObjectKey,
  getPnBucketName,
  getPnBucketRegion,
  getPnIndexObjectKey,
  getAlarmNDays,
  getMinBytesDataLakeFile,
  getRowsToLog,
};
