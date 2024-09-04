const core = require('@actions/core');
const github = require('@actions/github');
const fs = require('fs');
const xml2js = require('xml2js');

const {getDependencies} = require('./input-helper');

const POM_PATH = './pom.xml';
const OPENAPI_FILE_PATH = 'docs/openapi';

function getGitHubOpenapiRegexp(commitIds) {
    const dependencies = Object.keys(commitIds);
    const regexp = new RegExp(`^${getEnvVariable('OPENAPI_ROOT_PATH')}/${github.context.repo.owner}/(?<repository>${dependencies.join('|')})/(?<commitId>.+)/${OPENAPI_FILE_PATH}/(?<openapiFile>.+).yaml$`, 'g');
    core.debug(`Computed regular expression ${regexp.toString()}`);
    return regexp;
}

async function getPomVersion() {
    core.debug('Getting POM version')
    try {
        const content = fs.readFileSync(POM_PATH, 'utf8');
        const pomObject = await xml2js.parseStringPromise(xmlContent);
        core.info(JSON.stringify(pomObject));
        // core.debug(`POM version ${pomVersion}`);
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
            core.debug(`match ${match}`);
            core.debug(`repository ${repository}`);
            core.debug(`commitId ${commitId}`);
            return `${getEnvVariable('OPENAPI_ROOT_PATH')}/${github.context.repo.owner}/${repository}/${commitIds[repository]}/${OPENAPI_FILE_PATH}/${openapiFile}.yaml`
        });
        // save the content
        fs.writeFileSync(POM_PATH, content);
        core.info('POM updated successfully');
    } catch (error) {
        throw new Error(`Error reading pom: ${error}`);
    }
}

module.exports = {
    updatePom
}