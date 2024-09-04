const core = require('@actions/core');
const github = require('@actions/github');

const {getPomVersion} = require('./file-helper');

class RepositoryHelper {
    #BRANCH_NAME_ROOT = 'update-dependencies';
    #octokit;
    #branchSha;
    #branchName;

    constructor() {
        core.debug(`Init octokit client`);
        // initialize Octokit client
        const token = core.getInput('token');
        if (token) {
            this.#octokit = github.getOctokit(token);
            return;
        }
        throw new Error(`No GitHub token specified`);
    }

    async getLastTagCommitId(repositoryName) {
        core.debug(`Fetch list of tags for repository ${repositoryName}`);
        try {
            const {data: tags} = await this.#octokit.rest.repos.listTags({
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
            const branch = await this.#octokit.rest.git.getRef({
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
           const {data: branch} = await this.#octokit.rest.git.getRef({
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
            const {data: branchTree} = await this.#octokit.rest.git.getTree({
              owner: github.context.repo.owner,
              repo: github.context.repo.repo,
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
        this.#branchName = `${this.#BRANCH_NAME_ROOT}/${pomVersion}`;
        const branchExists = await this.#checkIfBranchExists(this.#branchName);
        const baseBranchName = core.getInput('ref', { required: true });
        core.debug(`Base branch: ${baseBranchName}`);
        if (!branchExists) {
            // Create a new branch based on the base branch
            const baseBranch = await this.#getBranchRef(baseBranchName);
            try {
                 const {data: branchRef} = await this.#octokit.rest.git.createRef({
                   owner: github.context.repo.owner,
                   repo: github.context.repo.repo,
                   ref: `refs/heads/${this.#branchName}`,
                   sha: baseBranch.object.sha
                 });
                 this.#branchSha = branchRef.object.sha;
            } catch(error) {
                throw new Error(`Error during branch creation: ${error}`);
            }
            return;
        }
        const baseBranch = await this.#getBranchRef(baseBranchName);
        const branchRef = this.#getBranchRef(this.#branchName);
        this.#branchSha = branchRef.object.sha;
    }

    async #updateRef(branchName, commitSha) {
        core.debug(`updating branch ${branchName} reference`);
        try {
            await this.#octokit.rest.git.updateRef({
              owner: github.context.repo.owner,
              repo: github.context.repo.repo,
              ref: `heads/${branchName}`,
              sha: commitSha,
            });
            core.debug(`branch ${branchName} reference updated`);
        } catch (error) {
            throw new Error(`Error during branch ${branchName} reference updating: ${error}`);
        }
    }

    async commitChanges() {
        core.info(`committing changes`);
        try {
            // get branch tree
            const branchTree = getBranchTree(this.#branchSha);
            const {data: commit}  = await this.#octokit.rest.git.createCommit({
              owner: github.context.repo.owner,
              repo: github.context.repo.repo,
              message: 'Update micro-service dependencies',
              tree: branchTree.sha,
              parents: [this.#branchSha]
            });
            await this.#updateRef(this.#branchName, commit.sha)
            core.debug(`changes committed`);
        } catch (error) {
            throw new Error(`Error during commit creation: ${error}`);
        }
    }
}

module.exports = {
    RepositoryHelper
}