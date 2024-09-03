const core = require('@actions/core');
const github = require('@actions/github');
const fs = require('fs');

const POM_PATH = './pom.xml';
const GITHUB_ROOT_PATH = 'https://raw.githubusercontent.com';
const GITHUB_OPENAPI_FILE_PATH = 'docs/openapi';

function getGitHubOpenapiRegexp(commitIds) {
    const dependencies = Object.keys(commitIds);
    const regexp = new RegExp(`${GITHUB_ROOT_PATH}/${github.context.repo.owner}/(?<repository>${dependencies.join('|')})/(?<commitId>.+)/${GITHUB_OPENAPI_FILE_PATH}/(?<openapiFile>.+).yaml`, 'g');
    core.debug(`Computed regular expression ${regexp.toString()}`);
    return regexp;
}

function updatePom(commitIds) {
    core.debug(`Reading pom at path ${POM_PATH}`);
    try {
        // read content
        let content = fs.readFileSync(POM_PATH, 'utf8');
        core.debug(`Updating pom`);
        // change content
        content = content.replace(getGitHubOpenapiRegexp(commitIds), (match, repository, commitId, openapiFile) => {
            core.debug(`match ${match}`);
            core.debug(`repository ${repository}`);
            core.debug(`commitId ${commitId}`);
            return `${GITHUB_ROOT_PATH}/${github.context.repo.owner}/${repository}/${commitIds[repository]}/${GITHUB_OPENAPI_FILE_PATH}/${openapiFile}.yaml`
        });
        // save the content
        fs.writeFileSync(POM_PATH, content);
    } catch (error) {
        throw new Error(`Error reading pom: ${error}`);
    }
}

module.exports = {
    updatePom
}