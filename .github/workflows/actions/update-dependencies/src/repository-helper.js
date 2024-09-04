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

async function checkIfBranchExists(branchName) {
    const octokit = initOctokitClient();
    core.debug(`checking if branch ${branchName} exists`);
    try {
        const branch = await octokit.rest.repos.getBranch({
          owner: github.context.repo.owner,
          repo: github.context.repo.repo,
          branch: branchName,
        });
        core.debug(`branch already ${branchName} exists`);
        return true;
    } catch (error) {
        if (error.status === 404) {
            return false;
        }
        throw new Error(`Error during branch ${branchName} retrieving: ${error}`);
    }
}

async function getBranch(branchName) {
    const octokit = initOctokitClient();
    core.debug(`getting branch ${branchName}`);
    try {
       const branch = await octokit.rest.repos.getBranch({
         owner: github.context.repo.owner,
         repo: github.context.repo.repo,
         branch: branchName,
       });
       core.info(JSON.stringify(branch));
       core.debug(`branch ${branchName} retrieved`);
       return branch;
    } catch (error) {
        throw new Error(`Error during branch ${branchName} retrieving: ${error}`);
    }
}

async function getBaseBranchTree(baseBranchSha) {
    const octokit = initOctokitClient();
    core.debug(`getting base branch tree`);
    try {
        const baseBranchTree = await octokit.rest.git.getTree({
          owner,
          repo,
          tree_sha: baseBranchSha
        });
        core.debug(`base branch tree retrieved`);
        return baseBranchTree;
   } catch (error) {
     throw new Error(`Error during base branch tree retrieving: ${error}`);
    }
}

async function createBranch() {
    const octokit = initOctokitClient();
    // first check if branch already exists
    const pomVersion = await getPomVersion();
    const branchName = `${BRANCH_NAME_ROOT}/${pomVersion}`
    const branchExists = await checkIfBranchExists(branchName);
    if (!branchExists) {
        // Create a new branch based on the base branch
        const baseBranchName = core.getInput('ref', { required: true });
        core.debug(`Base branch: ${baseBranchName}`);
        const baseBranch = await getBranch(baseBranchName);
        try {
         await octokit.rest.git.createRef({
           owner: github.context.repo.owner,
           repo: github.context.repo.repo,
           ref: `refs/heads/${branchName}`,
           sha: baseBranch.commit.sha
         });
        } catch(error) {
          throw new Error(`Error during branch creation: ${error}`);
        }
        return;
    }
}

module.exports = {
    getLastTagCommitId,
    createBranch
}