const core = require('@actions/core');
const github = require('@actions/github');

const {getPomVersion} = require('./file-helper');

class RepositoryHelper {
    #BRANCH_NAME_ROOT = 'update-dependencies';
    #octokit;

    constructor() {
        this.initOctokitClient();
    }

    #initOctokitClient() {
        core.debug(`Init octokit client`);
        // initialize Octokit client
        const token = core.getInput('token');
        if (token) {
            octokit = github.getOctokit(token);
        }
        throw new Error(`No GitHub token specified`);
    }

    async getLastTagCommitId(repositoryName) {
        core.debug(`Fetch list of tags for repository ${repositoryName}`);
        try {
            const {data: tags} = await this.octokit.rest.repos.listTags({
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

    async #checkIfBranchExists(branchName) {
        core.debug(`checking if branch ${branchName} exists`);
        try {
            const branch = await this.octokit.rest.repos.getRef({
              owner: github.context.repo.owner,
              repo: github.context.repo.repo,
              ref: `heads/${branchName}`,
            });
            core.debug(`branch already ${branchName} exists`);
            return true;
        } catch (error) {
            if (error.status === 404) {
                return false;
            }
            throw new Error(`Error during branch ${branchName} reference retrieving: ${error}`);
        }
    }

    async #getBranchRef(branchName) {
        core.debug(`getting branch ${branchName} reference`);
        try {
           const {data: branch} = await this.octokit.rest.repos.getRef({
             owner: github.context.repo.owner,
             repo: github.context.repo.repo,
             ref: `heads/${branchName}`,
           });
           core.debug(`branch ${branchName} reference retrieved`);
           return branch;
        } catch (error) {
            throw new Error(`Error during branch ${branchName} reference retrieving: ${error}`);
        }
    }

    async #getBranchTree(branchSha) {
        core.debug(`getting branch tree`);
        try {
            const {data: branchTree} = await this.octokit.rest.git.getTree({
              owner,
              repo,
              tree_sha: branchSha
            });
            core.debug(`branch tree retrieved`);
            return branchTree;
       } catch (error) {
         throw new Error(`Error during branch tree retrieving: ${error}`);
        }
    }

    async createBranch() {
        // first check if branch already exists
        const pomVersion = await getPomVersion();
        const branchName = `${BRANCH_NAME_ROOT}/${pomVersion}`
        const branchExists = await this.checkIfBranchExists(branchName);
        if (!branchExists) {
            // Create a new branch based on the base branch
            const baseBranchName = core.getInput('ref', { required: true });
            core.debug(`Base branch: ${baseBranchName}`);
            const baseBranch = await this.getBranchRef(baseBranchName);
            try {
             await this.octokit.rest.git.createRef({
               owner: github.context.repo.owner,
               repo: github.context.repo.repo,
               ref: `refs/heads/${branchName}`,
               sha: baseBranch.object.sha
             });
            } catch(error) {
              throw new Error(`Error during branch creation: ${error}`);
            }
            return;
        }
    }

    async commitChanges() {

    }
}

module.exports = {
    RepositoryHelper
}