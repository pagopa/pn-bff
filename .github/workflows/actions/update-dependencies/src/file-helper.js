const core = require('@actions/core');
const github = require('@actions/github');
const fs = require('fs');
const xml2js = require('xml2js');

const {InputHelper} = require('./input-helper');

class FileHelper {

    #POM_PATH = './pom.xml';
    #OPENAPI_FILE_PATH = 'docs/openapi';

    #getGitHubOpenapiRegexp(commitIds) {
         const dependencies = Object.keys(commitIds);
         const regexp = new RegExp(`${InputHelper.getEnvVariable('OPENAPI_ROOT_PATH')}/${github.context.repo.owner}/(?<repository>${dependencies.join('|')})/(?<commitId>.+)/${this.#OPENAPI_FILE_PATH}/(?<openapiFile>.+).yaml`, 'g');
         core.debug(`Computed regular expression ${regexp.toString()}`);
         return regexp;
    }

     async getPomVersion() {
         core.debug('Getting POM version')
         try {
             const content = fs.readFileSync(this.#POM_PATH, 'utf8');
             const pomObject = await xml2js.parseStringPromise(content);
             const pomVersion = pomObject.project.version;
             core.debug(`POM version ${pomVersion}`);
             return pomVersion;
         } catch (error) {
           throw new Error(`Error reading pom: ${error}`);
         }
     }

     updatePom(commitIds) {
         core.info('Updating POM');
         core.debug(`Reading pom at path ${this.#POM_PATH}`);
         try {
             // read content
             let content = fs.readFileSync(this.#POM_PATH, 'utf8');
             core.debug(`Updating pom`);
             // change content
             content = content.replace(this.#getGitHubOpenapiRegexp(commitIds), (match, repository, commitId, openapiFile) => {
                 core.debug(`Match ${match}`);
                 core.debug(`Repository ${repository}`);
                 core.debug(`CommitId ${commitId}`);
                 return `${InputHelper.getEnvVariable('OPENAPI_ROOT_PATH')}/${github.context.repo.owner}/${repository}/${commitIds[repository]}/${this.#OPENAPI_FILE_PATH}/${openapiFile}.yaml`
             });
             core.info('POM updated successfully');
             return content
         } catch (error) {
             throw new Error(`Error reading pom: ${error}`);
         }
     }
}

module.exports = {
    FileHelper
}