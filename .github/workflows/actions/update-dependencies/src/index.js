const core = require('@actions/core');
const github = require('@actions/github');

const {getInputs} = require('./input-helper');

try {
  // read inputs
  const inputs = getInputs();
  // output
  core.setOutput("dependencies", []);
} catch (error) {
  core.setFailed(error.message);
}