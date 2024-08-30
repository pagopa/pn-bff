const core = require('@actions/core');
const github = require('@actions/github');

const {getDependencies} = require('./input-helper');

try {
  // read inputs
  const dependencies = getDependencies();
  if (dependencies.length > 0) {
    core.info(`Chosen dependencies to update = ${dependencies.join(', ')}`);
    return;
  }
  throw new Error(`No dependencies chosen`);
} catch (error) {
  core.setFailed(error.message);
}