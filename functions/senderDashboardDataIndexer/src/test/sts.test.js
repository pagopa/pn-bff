import { getAssumeRoleCredentials } from '../app/sts.js';
import { expect } from 'chai';
import { mockClient } from 'aws-sdk-client-mock';

import { STSClient, AssumeRoleCommand } from '@aws-sdk/client-sts';

describe('sts tests', function () {
  it('getAssumeRoleCredentials', async () => {
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
      'testSession',
    );

    // Then
    expect(credentials).to.be.deep.equal(mockCredentials);

    stsMock.reset();
  });
});
