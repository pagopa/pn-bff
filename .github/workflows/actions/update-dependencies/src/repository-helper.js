const core = require('@actions/core');
const github = require('@actions/github');

const {getPomVersion} = require('./file-helper');

const BRANCH_NAME_ROOT = 'update-dependencies';

function initOctokitClient() {
    core.debug(`Init octokit client`);
    // initialize Octokit client
    const token = core.getInput('token');
    if (token) {
        const octokit = github.getOctokit(token);
        return octokit;
    }
    throw new Error(`No GitHub token specified`);
}

async function getLastTagCommitId(repositoryName) {
    core.debug(`Fetch list of tags for repository ${repositoryName}`);
    const octokit = initOctokitClient();
    try {
        const {data: tags} = await octokit.rest.repos.listTags({
          owner: github.context.repo.owner,
          repo: repositoryName,
          per_page: 1
        });
        if (tags.length > 0) {
            core.info(`Retrieved tag info for repository ${repositoryName}: version - ${tags[0].name} and commitId - ${tags[0].commit.sha}`);
            return tags[0].commit.sha;
        }
        throw new Error(`No tag found for repository ${repositoryName}`);
    } catch(error) {
        throw new Error(`Error during tag retrieving: ${error}`);
    }
}

async function checkIfBranchExists() {
    try {
        const pomVersion = await getPomVersion();
        const branchName = `${BRANCH_NAME_ROOT}/${pomVersion}`
        core.debug(`checking if branch ${branchName} exists`);
        const branch = await octokit.rest.repos.getBranch({
          owner: github.context.repo.owner,
          repo: github.context.repo.repo,
          branch: branchName,
        });
        core.debug(`branch already ${branchName} exists`);
        return true;
    } catch (error) {
        core.info(error);
        return false;
    }
}

async function createBranch() {
    const octokit = initOctokitClient();
    try {
        // first check if branch already exists
        const branchExists = checkIfBranchExists();
        core.info(branchExists);
        const defaultBranch = core.getInput('ref', { required: true });
        core.debug(`Default branch: ${defaultBranch}`);
        /*
        // Create a new branch based on the default branch
        await octokit.rest.git.createRef({
          owner: github.context.repo.owner,
          repo: github.context.repo.repo,
          ref: `refs/heads/${newBranchName}`,
          sha: baseBranchSha
        });
        */
    } catch(error) {
      throw new Error(`Error during branch creation: ${error}`);
    }
}

module.exports = {
    getLastTagCommitId,
    createBranch
}