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
        const dependenciesMatchingGroup = dependencies.reduce((matchingString, dependency, index) => {
            matchingString += `(?<${dependency.replace('-', '_')}>${dependency}/(.+))`
            if (index < dependencies.length - 1) {
                matchingString += '|';
            }
            return matchingString;
        }, '');
        const regexp = new RegExp(`${GITHUB_ROOT_PATH}/${github.context.repo.owner}/${dependenciesMatchingGroup}/${GITHUB_OPENAPI_FILE_PATH}/.+.yaml`, 'g');
        content.replace(regexp, (a, b) => {
            core.info(a);
            core.info(b);
        })
    } catch (error) {
        throw new Error(`Error reading pom: ${error}`);
    }
}

module.exports = {
    updatePom
}