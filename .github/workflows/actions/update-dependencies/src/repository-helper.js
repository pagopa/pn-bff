const core = require('@actions/core');
const github = require('@actions/github');

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
    core.info(github.context.repo.owner);
    try {
        const tags = await octokit.rest.repos.listTags({
          owner: github.context.repo.owner,
          repository: `pagopa/${repositoryName}`,
        });
        return repositoryName;
    } catch(error) {
        throw new Error(`Error during tag retrieving: ${error}`);
    }
}

module.exports = {
    initOctokitClient,
    getLastTagCommitId
}