const core = require('@actions/core');
const github = require('@actions/github');

const {getDependencies} = require('./input-helper');

try {
  // read inputs
  const dependencies = getDependencies();
  core.debug(`Chosen dependencies to update = ${dependencies.join(', ')}`);
} catch (error) {
  core.setFailed(error.message);
}