const core = require('@actions/core');
const {github} = require('@actions/github');

let octokit;

function initOctokitClient() {
    core.debug(`Init octokit client`);
    // initialize Octokit client
    const token = core.getInput('token');
    if (token) {
        octokit = github.getOctokit(token);
        return;
    }
    throw new Error(`No GitHub token specified`);
}

async function getLastTagCommitId(repositoryName) {
    core.debug(`Fetch list of tags for repository ${repositoryName}`);
    try {
        const tags = await octokit.rest.repos.listTags({
          owner: 'octokit',
          repository: repositoryName,
        });
        return repositoryName;
    } catch(e) {
        throw new Error(`Error during tag retrieving`, e);
    }
}

module.exports = {
    initOctokitClient,
    getLastTagCommitId
}