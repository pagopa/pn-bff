const {github} = require('@actions/github');

function getLastTagCommitId(repositoryName) {
/*
const octokit = github.getOctokit(myToken)
    octokit.rest.repos.listTags({
      owner,
      repo,
    });
    */
    return repositoryName;
}

module.exports = {
    getLastTagCommitId
}