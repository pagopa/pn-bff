const core = require('@actions/core');
const github = require('@actions/github');

try {
  // read inputs
  const authFleet = core.getInput('pn-auth-fleet');
  console.log(authFleet);
  // output
  core.setOutput("dependencies", []);
} catch (error) {
  core.setFailed(error.message);
}