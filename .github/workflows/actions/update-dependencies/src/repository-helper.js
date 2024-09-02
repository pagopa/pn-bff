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

module.exports = {
    initOctokitClient,
    getLastTagCommitId
}