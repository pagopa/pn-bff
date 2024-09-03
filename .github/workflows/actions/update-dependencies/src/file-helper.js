const core = require('@actions/core');
const github = require('@actions/github');
const fs = require('fs');

const POM_PATH = './pom.xml';
const GITHUB_ROOT_PATH = 'https://raw.githubusercontent.com';
const GITHUB_OPENAPI_FILE_PATH = 'docs/openapi';

function updatePom(commitIds) {
    core.debug(`Reading pom at path ${POM_PATH}`);
    try {
        let content = fs.readFileSync(POM_PATH, 'utf8');
        core.debug(`Updating pom`);
        const dependencies = Object.keys(commitIds);
        const regexp = new RegExp(`${GITHUB_ROOT_PATH}/${github.context.repo.owner}/(?<repository>${dependencies.join('|')})/(?<commitId>.+)/${GITHUB_OPENAPI_FILE_PATH}/.+.yaml`, 'g');
        core.debug(`Computed regular expression ${regexp.toString()}`);
        content.replace(regexp, (match, repository, commitId) => {
            core.info(`match ${match}`);
            core.info(`repository ${repository}`);
            core.info(`commitId ${commitId}`);
        })
    } catch (error) {
        throw new Error(`Error reading pom: ${error}`);
    }
}

module.exports = {
    updatePom
}