const { STSClient, AssumeRoleCommand } = require('@aws-sdk/client-sts');

/**
 * Gets temporary security credentials by assuming IAM role.
 *
 * @return {Promise<Object>} Temporary security credentials: Access Key ID,
 * Secret Access Key and Session Token
 */
const getAssumeRoleCredentials = async (
  roleArn,
  roleSessionName,
  region = 'eu-south-1'
) => {
  const stsClient = new STSClient({ region: region });
  const assumeRoleCommand = new AssumeRoleCommand({
    RoleArn: roleArn,
    RoleSessionName: roleSessionName,
  });

  const { Credentials } = await stsClient.send(assumeRoleCommand);
  return {
    accessKeyId: Credentials.AccessKeyId,
    secretAccessKey: Credentials.SecretAccessKey,
    sessionToken: Credentials.SessionToken,
  };
};

module.exports = { getAssumeRoleCredentials };
