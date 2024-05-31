const { SSMClient, GetParameterCommand } = require('@aws-sdk/client-ssm');

const client = new SSMClient({ region: process.env.AWS_REGION });

const getParameter = async (parameterName) => {
  try {
    console.log(`Retrieving parameter: ${parameterName}`);
    const command = new GetParameterCommand({ Name: parameterName });
    const response = await client.send(command);
    console.log(`Successfully retrieved parameter: ${parameterName}`);
    return response.Parameter.Value;
  } catch (error) {
    console.error('Error retrieving SSM parameter:', error);
    throw error;
  }
}

module.exports = { getParameter };
