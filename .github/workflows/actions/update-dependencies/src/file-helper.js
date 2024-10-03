const core = require('@actions/core');
const github = require('@actions/github');
const xml2js = require('xml2js');

class FileHelper {

    #repositoryHelper;
    #OPENAPI_FILE_PATH = 'docs/openapi';

    constructor(repositoryHelper) {
        this.#repositoryHelper = repositoryHelper;
    }

    #getGitHubOpenapiRegexp(tags) {
         const dependencies = Object.keys(tags);
         const regexp = new RegExp(`https://raw\\.githubusercontent\\.com/${github.context.repo.owner}/(?<repository>${dependencies.join('|')})/(?<commitId>.+)/${this.#OPENAPI_FILE_PATH}/(?<openapiFile>.+)\\.yaml`, 'g');
         core.debug(`Computed regular expression ${regexp.toString()}`);
         return regexp;
    }

    #getInternalDependenciesRegexp(tags) {
        const dependencies = Object.keys(tags);
        const regexp = new RegExp(`(<dependency>\\s*<groupId>it\\.pagopa\\.pn</groupId>\\s*<artifactId>(?<repository>${dependencies.join('|')})</artifactId>\\s*<version>)(?<version>\\d\\.\\d\\.\\d)(</version>\\s*</dependency>)`, 'g');
         core.debug(`Computed regular expression ${regexp.toString()}`);
         return regexp;
    }

     async getPomVersion() {
         core.debug('Getting POM version')
         try {
             const baseBranchName = core.getInput('ref', { required: true });
             const content = await this.#repositoryHelper.getFileContent(baseBranchName, 'pom.xml');
             const pomObject = await xml2js.parseStringPromise(content);
             const pomVersion = pomObject.project.version;
             core.debug(`POM version ${pomVersion}`);
             return pomVersion;
         } catch (error) {
           throw new Error(`Error reading pom: ${error}`);
         }
     }

     async updatePom(branchName, tags) {
         core.info('Updating POM');
         try {
             // read content
             let content = await this.#repositoryHelper.getFileContent(branchName, 'pom.xml');
             core.debug(`Updating POM content`);
             // change content
             // update microservices dependencies
             let pomUpdated = false;
             content = content.replace(this.#getGitHubOpenapiRegexp(tags), (match, repository, commitId, openapiFile) => {
                 core.debug(`Match ${match}, Repository ${repository} and CommitId ${commitId}`);
                 if (commitId !== tags[repository].commitId) {
                    pomUpdated = true;
                    return `https://raw.githubusercontent.com/${github.context.repo.owner}/${repository}/${tags[repository].commitId}/${this.#OPENAPI_FILE_PATH}/${openapiFile}.yaml`;
                 }
                 return match;
             });
             // update internal packages version
             content = content.replace(this.#getInternalDependenciesRegexp(tags), (match, preVersion, repository, version, postVersion) => {
                core.debug(`Match ${match}, Repository ${repository} and Version ${version}`);
                // tag name contains v at start
                const newVersion = tags[repository].name.replace('v', '');
                if (version !== newVersion) {
                   pomUpdated = true;
                   return `${preVersion}${newVersion}${postVersion}`;
                }
                return match;
            });
             if (pomUpdated) {
                core.info('POM updated successfully');
                return content;
             }
             core.info('POM already updated');
             return null;
         } catch (error) {
             throw new Error(`Error reading pom: ${error}`);
         }
     }

     async updateOpenapi(branchName, tags) {
        core.info('Updating openapi files');
        const files = await this.#repositoryHelper.getDirContent(branchName, this.#OPENAPI_FILE_PATH, ['aws', 'api-external']);
        if (files.length === 0) {
            core.info('No openapi file to update');
            return;
        }
        core.debug(`Files found at path ${this.#OPENAPI_FILE_PATH}: ${files.map(file => file.path).join(', ')}`);
        const filesToUpdate = [];
        for (const file of files) {
            try {
                core.info(`Updating openapi file at path ${file.path}`);
                // read content
                let content = file.content;
                // change content
                let fileUpdated = false;
                content = content.replace(this.#getGitHubOpenapiRegexp(tags), (match, repository, commitId, openapiFile) => {
                    core.debug(`Match ${match}, Repository ${repository} and CommitId ${commitId}`);
                    if (commitId !== tags[repository].commitId) {
                        fileUpdated = true;
                        return `https://raw.githubusercontent.com/${github.context.repo.owner}/${repository}/${tags[repository].commitId}/${this.#OPENAPI_FILE_PATH}/${openapiFile}.yaml`;
                    }
                    return match;
                });
                if (fileUpdated) {
                    core.info(`Openapi file at path ${file.path} updated successfully`);
                    filesToUpdate.push({path: file.path, content})
                    continue;
                }
                core.info(`Openapi file at path ${file.path} already updated`);
            } catch (error) {
                throw new Error(`Error reading openapi file at path ${file.path}: ${error}`);
            }
        }
        if (filesToUpdate.length > 0) {
            core.info('Openapi files updated successfully');
            return filesToUpdate;
        }
        core.info('Openapi files already updated');
        return filesToUpdate;
     }
}

module.exports = {
    FileHelper
}