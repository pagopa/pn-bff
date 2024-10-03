const core = require('@actions/core');
const github = require('@actions/github');

const {InputHelper} = require('./input-helper');
const {RepositoryHelper} = require('./repository-helper');
const {FileHelper} = require('./file-helper');

const BRANCH_NAME_ROOT = 'update-dependencies';

async function run() {
    // init helpers
    const repositoryHelper = new RepositoryHelper();
    const fileHelper = new FileHelper(repositoryHelper);
    try {
      // read dependencies to update
      const dependencies = InputHelper.getDependencies();
      if (dependencies.length > 0) {
        core.info(`Chosen dependencies to update = ${dependencies.join(', ')}`);
        // for those dependencies chosen get the commit id and the name of the last tag
        const tags = {};
        for (const dependency of dependencies) {
            core.info(`-------------------- ${dependency} --------------------`);
            tags[dependency] = await repositoryHelper.getLastTag(dependency);
        };
        // create branch
        const pomVersion = await fileHelper.getPomVersion();
        const branchName = `${BRANCH_NAME_ROOT}/${pomVersion}`;
        await repositoryHelper.createBranch(branchName);
        // update pom
        const pomContent = await fileHelper.updatePom(branchName, tags);
        // update openapi files
        const openapiFiles = await fileHelper.updateOpenapi(branchName, tags);
        // commit changes
        let changesToCommit = [];
        if (pomContent) {
            changesToCommit.push({path: 'pom.xml', content: pomContent});
        }
        changesToCommit = changesToCommit.concat(openapiFiles);
        await repositoryHelper.commitChanges(branchName, changesToCommit);
        // create pull request
        await repositoryHelper.createPullRequest(branchName, changesToCommit);
        return;
      }
      throw new Error(`No dependencies chosen`);
    } catch (error) {
      core.setFailed(error.message);
    }
}

run();