const core = require('@actions/core');
const github = require('@actions/github');

const {getDependencies} = require('./input-helper');
const {getLastTagCommitId} = require('./repository-helper');
const {updatePom} = require('./file-helper');

async function run() {
    try {
      // read dependencies to update
      const dependencies = getDependencies();
      if (dependencies.length > 0) {
        core.info(`Chosen dependencies to update = ${dependencies.join(', ')}`);
        // for those dependencies chosen get the commit id of the last tag
        const commitIds = {};
        for (const dependency of dependencies) {
            core.info(`-------------------- ${dependency} --------------------`);
            commitIds[dependency] = await getLastTagCommitId(dependency);
        };
        // update pom
        updatePom(commitIds);

        return;
      }
      throw new Error(`No dependencies chosen`);
    } catch (error) {
      core.setFailed(error.message);
    }
}

run();