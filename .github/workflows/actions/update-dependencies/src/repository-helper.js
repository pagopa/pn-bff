const core = require('@actions/core');
const github = require('@actions/github');

class RepositoryHelper {
    #octokit;
    #branchSha;

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

    async getLastTag(repositoryName) {
        core.debug(`Fetch list of tags for repository ${repositoryName}`);
        try {
            const {data: tags} = await this.#octokit.rest.repos.listTags({
              owner: github.context.repo.owner,
              repo: repositoryName,
              per_page: 1
            });
            if (tags.length > 0) {
                core.info(`Retrieved tag info for repository ${repositoryName}: version - ${tags[0].name} and commitId - ${tags[0].commit.sha}`);
                return {commitId: tags[0].commit.sha, tag: tags[0].name};
            }
            throw new Error(`No tag found for repository ${repositoryName}`);
        } catch(error) {
            throw new Error(`Error during tag retrieving: ${error}`);
        }
    }

    async #checkIfBranchExists(branchName) {
        core.debug(`Checking if branch ${branchName} exists`);
        try {
            await this.#octokit.rest.git.getRef({
              owner: github.context.repo.owner,
              repo: github.context.repo.repo,
              ref: `heads/${branchName}`,
            });
            core.debug(`Branch ${branchName} already exists`);
            return true;
        } catch (error) {
            if (error.status === 404) {
                core.debug(`Branch ${branchName} does not exist`);
                return false;
            }
            throw new Error(`Error during branch ${branchName} reference retrieving: ${error}`);
        }
    }

    async #getBranchRef(branchName) {
        core.debug(`Getting branch ${branchName} reference`);
        try {
           const {data: branchRef} = await this.#octokit.rest.git.getRef({
             owner: github.context.repo.owner,
             repo: github.context.repo.repo,
             ref: `heads/${branchName}`,
           });
           core.debug(`Branch ${branchName} reference retrieved`);
           return branchRef;
        } catch (error) {
            throw new Error(`Error during branch ${branchName} reference retrieving: ${error}`);
        }
    }

    async createBranch(branchName) {
        core.info(`Creating branch ${branchName}`);
        // first check if branch already exists
        const branchExists = await this.#checkIfBranchExists(branchName);
        const baseBranchName = core.getInput('ref', { required: true });
        core.debug(`Base branch: ${baseBranchName}`);
        if (!branchExists) {
            // Create a new branch based on the base branch
            const baseBranchRef = await this.#getBranchRef(baseBranchName);
            try {
                 const {data: branchRef} = await this.#octokit.rest.git.createRef({
                   owner: github.context.repo.owner,
                   repo: github.context.repo.repo,
                   ref: `refs/heads/${branchName}`,
                   sha: baseBranchRef.object.sha
                 });
                 this.#branchSha = branchRef.object.sha;
                 core.debug(`Branch sha ${this.#branchSha}`);
                 core.info(`Branch ${branchName} created`);
            } catch(error) {
                throw new Error(`Error during branch creation: ${error}`);
            }
            return;
        }
        const branchRef = await this.#getBranchRef(branchName);
        this.#branchSha = branchRef.object.sha;
        core.debug(`Branch sha ${this.#branchSha}`);
        core.info(`Branch ${branchName} already exists`);
    }

    async getFileContent(branchName, filePath) {
        core.info(`Reading file at path ${filePath}`);
        try {
            const {data: file} = await this.#octokit.rest.repos.getContent({
              owner: github.context.repo.owner,
              repo: github.context.repo.repo,
              ref: `heads/${branchName}`,
              path: filePath,
            });
            core.info(`File at path ${filePath} read`);
            return Buffer.from(file.content, 'base64').toString('utf-8');
        } catch (error) {
            throw new Error(`Error reading file at path ${filePath}: ${error}`);
        }
    }

    async getDirContent(branchName, dirPath, excluded = []) {
        core.info(`Reading content of dir at path ${dirPath}`);
        try {
            let files = [];
            const {data: directory} = await this.#octokit.rest.repos.getContent({
              owner: github.context.repo.owner,
              repo: github.context.repo.repo,
              ref: `heads/${branchName}`,
              path: dirPath,
            });
            core.info(`Content of dir at path ${dirPath} read`);
            for (const elem of directory) {
                if (excluded.some(excl => elem.name.includes(excl))) {
                    continue;
                }
                if (elem.type === 'file') {
                    const fileContent = await this.getFileContent(branchName, elem.path);
                    files.push({path: elem.path, content: fileContent});
                } else if (elem.type === 'dir') {
                    const dirContent = await this.getDirContent(branchName, elem.path, excluded);
                    files = files.concat(dirContent);
                }
            }
            return files;
        } catch (error) {
            throw new Error(`Error reading content of dir at path ${dirPath}: ${error}`);
        }
    }

    async #createBlob(fileContent) {
        core.debug(`Creating blob from file content`);
        try {
            const {data: blob} = await this.#octokit.rest.git.createBlob({
              owner: github.context.repo.owner,
              repo: github.context.repo.repo,
              content: fileContent,
            });
            core.debug(`Blob created with sha ${blob.sha}`);
            return blob;
        } catch (error) {
            throw new Error(`Error during blob creation: ${error}`);
        }
    }

    async #createBranchTree(branchSha, files) {
        core.debug(`Creating branch tree`);
        const shaFiles = [];
        for (const file of files) {
            const blob = await this.#createBlob(file.content);
            shaFiles.push({
                path: file.path,
                sha: blob.sha
            })
        }
        try {
            const {data: branchTree} = await this.#octokit.rest.git.createTree({
              owner: github.context.repo.owner,
              repo: github.context.repo.repo,
              tree: shaFiles.map((file) => ({
                  path: file.path,
                  mode: '100644',
                  type: 'blob',
                  sha: file.sha
              })),
              base_tree: branchSha
            });
            core.debug(`Branch tree created`);
            return branchTree;
       } catch (error) {
         throw new Error(`Error during branch tree creation: ${error}`);
        }
    }

    async #updateBranchRef(branchName, commitSha) {
        core.debug(`Updating branch ${branchName} reference`);
        try {
            await this.#octokit.rest.git.updateRef({
              owner: github.context.repo.owner,
              repo: github.context.repo.repo,
              ref: `heads/${branchName}`,
              sha: commitSha,
            });
            core.debug(`Branch ${branchName} reference updated`);
        } catch (error) {
            throw new Error(`Error during branch ${branchName} reference updating: ${error}`);
        }
    }

    async commitChanges(branchName, files) {
        if (files.length === 0) {
            core.info(`Nothing to commit on branch ${branchName}`);
            return;
        }
        core.info(`Committing changes on branch ${branchName}`);
        // get branch tree
        const branchTree = await this.#createBranchTree(this.#branchSha, files);
        core.debug(`Branch tree sha ${branchTree.sha}`);
        let commit;
        try {
            const response = await this.#octokit.rest.git.createCommit({
              owner: github.context.repo.owner,
              repo: github.context.repo.repo,
              message: 'Update micro-service dependencies',
              tree: branchTree.sha,
              parents: [this.#branchSha]
            });
            core.info(`Changes on branch ${branchName} committed`);
            commit = response.data;
        } catch (error) {
            throw new Error(`Error during commit creation: ${error}`);
        }
        core.info(`Pushing changes on branch ${branchName}`);
        await this.#updateBranchRef(branchName, commit.sha)
        core.info(`Changes on branch ${branchName} pushed`);
    }

    #getPullRequestBodyString(changesToCommit) {
        let body = "## Short description\n\nUpdated micro-service dependencies\n\n## List of changes proposed in this pull request\n\n";
        for (const change of changesToCommit) {
            body += `- Updated ${change.path}\n`;
        }
        body += "\n## How to test\n\nRun a clean install -> run tests -> check that everything is ok\n\n>[!WARNING]\n>**IMPORTANT: remember to regenerate the external and aws files**";
        return body;
    }

    async #checkIfPullRequestExists(branchName) {
        core.info(`Check if an opened pull request already exists`);
        try {
           const baseBranchName = core.getInput('ref', { required: true });
           const {data: pullRequests} = await this.#octokit.rest.pulls.list({
             owner: github.context.repo.owner,
             repo: github.context.repo.repo,
             head: `${github.context.repo.owner}:${branchName}`,
             base: baseBranchName,
             per_page: 1
           });
           if (pullRequests.length === 0) {
            core.info("Opened pull request doesn't exists");
            return null;
           }
           core.info(`An opened pull request already exists`);
           return pullRequests[0];
        } catch (error) {
            throw new Error(`Error during pull request retrieving: ${error}`);
        }
    }

    async #updatePullRequest(pullNumber, changesToCommit) {
        core.info('Updating pull request');
        try {
            await this.#octokit.rest.pulls.update({
                owner: github.context.repo.owner,
                repo: github.context.repo.repo,
                pull_number: pullNumber,
                body: this.#getPullRequestBodyString(changesToCommit),
            });
            core.info('Pull request updated');
        } catch (error) {
            throw new Error(`Error during pull request updating: ${error}`);
        }
    }

    async createPullRequest(branchName, changesToCommit) {
        if (changesToCommit.length === 0) {
            return;
        }
        // check if pull request already exists
        const pullRequest = await this.#checkIfPullRequestExists(branchName);
        if (pullRequest) {
            // update pull request
            await this.#updatePullRequest(pullRequest.number, changesToCommit);
            return;
        }
        core.info(`Creating pull request`);
        try {
           const baseBranchName = core.getInput('ref', { required: true });
           await this.#octokit.rest.pulls.create({
             owner: github.context.repo.owner,
             repo: github.context.repo.repo,
             title: '[GitBot] - Update micro-service dependencies',
             body: this.#getPullRequestBodyString(changesToCommit),
             head: branchName,
             base: baseBranchName,
           });
           core.info(`Pull request created`);
        } catch (error) {
            throw new Error(`Error during pull request creation: ${error}`);
        }
    }
}

module.exports = {
    RepositoryHelper
}