const core = require('@actions/core');
const github = require('@actions/github');

function initOctokitClient() {
    core.debug(`Init octokit client`);
    // initialize Octokit client
    const token = core.getInput('token');
    if (token) {
        const octokit = github.getOctokit(token);
        core.info('---------------------');
        console.log(octokit);
        return octokit;
    }
    throw new Error(`No GitHub token specified`);
}

async function getLastTagCommitId(repositoryName) {
    core.debug(`Fetch list of tags for repository ${repositoryName}`);
    const octokit = initOctokitClient();
    try {
        const tags = await octokit.rest.repos.listTags({
          owner: github.context.repo.owner,
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