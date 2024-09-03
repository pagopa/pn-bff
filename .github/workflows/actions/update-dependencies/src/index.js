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
        dependencies.forEach(async (dependency) => {
            core.info(`-------------------- ${dependency} --------------------`);
            commitIds[dependency] = await getLastTagCommitId(dependency);
        });
        // update pom
        await updatePom('./pom.xml');

        return;
      }
      throw new Error(`No dependencies chosen`);
    } catch (error) {
      core.setFailed(error.message);
    }
}

run();