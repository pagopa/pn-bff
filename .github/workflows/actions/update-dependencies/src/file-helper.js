const core = require('@actions/core');
const github = require('@actions/github');
const fs = require('fs');
const path = require('path');
const xml2js = require('xml2js');

const {getDependencies, getEnvVariable} = require('./input-helper');

const POM_FILE = 'pom.xml';
const POM_PATH = path.join(__dirname, POM_FILE);
const OPENAPI_FILE_PATH = 'docs/openapi';

function getGitHubOpenapiRegexp(commitIds) {
    const dependencies = Object.keys(commitIds);
    const regexp = new RegExp(`${getEnvVariable('OPENAPI_ROOT_PATH')}/${github.context.repo.owner}/(?<repository>${dependencies.join('|')})/(?<commitId>.+)/${OPENAPI_FILE_PATH}/(?<openapiFile>.+).yaml`, 'g');
    core.debug(`Computed regular expression ${regexp.toString()}`);
    return regexp;
}

async function getPomVersion() {
    core.debug('Getting POM version')
    try {
        const content = fs.readFileSync(POM_PATH, 'utf8');
        const pomObject = await xml2js.parseStringPromise(content);
        const pomVersion = pomObject.project.version;
        core.debug(`POM version ${pomVersion}`);
        return pomVersion;
    } catch (error) {
      throw new Error(`Error reading pom: ${error}`);
    }
}

function updatePom(commitIds) {
    core.info('Updating POM');
    core.debug(`Reading pom at path ${POM_PATH}`);
    try {
        // read content
        let content = fs.readFileSync(POM_PATH, 'utf8');
        core.debug(`Updating pom`);
        // change content
        content = content.replace(getGitHubOpenapiRegexp(commitIds), (match, repository, commitId, openapiFile) => {
            core.debug(`Match ${match}`);
            core.debug(`Repository ${repository}`);
            core.debug(`CommitId ${commitId}`);
            return `${getEnvVariable('OPENAPI_ROOT_PATH')}/${github.context.repo.owner}/${repository}/${commitIds[repository]}/${OPENAPI_FILE_PATH}/${openapiFile}.yaml`
        });
        // save the content
        // fs.writeFileSync(POM_PATH, content);
        core.info('POM updated successfully');
        return {content, POM_FILE};
    } catch (error) {
        throw new Error(`Error reading pom: ${error}`);
    }
}

module.exports = {
    getPomVersion,
    updatePom
}