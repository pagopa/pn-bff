const { getAssumeRoleCredentials } = require('../app/sts.js');
const { expect } = require('chai');
const { mockClient } = require('aws-sdk-client-mock');

const { STSClient, AssumeRoleCommand } = require('@aws-sdk/client-sts');

describe('sts tests', function () {
  it('should getAssumeRoleCredentials', async () => {
    // Given
    const mockCredentials = {
      accessKeyId: 'test0',
      secretAccessKey: 'test1',
      sessionToken: 'test2',
    };
    const stsMock = mockClient(STSClient);
    stsMock.on(AssumeRoleCommand).resolves({
      Credentials: {
        AccessKeyId: mockCredentials.accessKeyId,
        SecretAccessKey: mockCredentials.secretAccessKey,
        SessionToken: mockCredentials.sessionToken,
      },
    });

    // When
    const credentials = await getAssumeRoleCredentials(
      'testRole',
      'testSession'
    );

    // Then
    expect(credentials).to.be.deep.equal(mockCredentials);

    stsMock.reset();
  });
});
