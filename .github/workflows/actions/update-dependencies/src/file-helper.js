const core = require('@actions/core');
const github = require('@actions/github');
const fs = require('fs');
const xml2js = require('xml2js');

const {InputHelper} = require('./input-helper');

class FileHelper {

    #repositoryHelper;
    #OPENAPI_FILE_PATH = 'docs/openapi';

    constructor(repositoryHelper) {
        this.#repositoryHelper = repositoryHelper;
    }

    #getGitHubOpenapiRegexp(commitIds) {
         const dependencies = Object.keys(commitIds);
         const regexp = new RegExp(`https://raw.githubusercontent.com/${github.context.repo.owner}/(?<repository>${dependencies.join('|')})/(?<commitId>.+)/${this.#OPENAPI_FILE_PATH}/(?<openapiFile>.+).yaml`, 'g');
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

     async updatePom(branchName, commitIds) {
         core.info('Updating POM');
         try {
             // read content
             let content = await this.#repositoryHelper.getFileContent(branchName, 'pom.xml');
             core.debug(`Updating POM content`);
             // change content
             let pomUpdated = false;
             content = content.replace(this.#getGitHubOpenapiRegexp(commitIds), (match, repository, commitId, openapiFile) => {
                 core.debug(`Match ${match}, Repository ${repository} and CommitId ${commitId}`);
                 if (commitId !== commitIds[repository]) {
                    pomUpdated = true;
                    return `https://raw.githubusercontent.com/${github.context.repo.owner}/${repository}/${commitIds[repository]}/${this.#OPENAPI_FILE_PATH}/${openapiFile}.yaml`;
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

     async updateOpenapi(branchName, commitIds) {
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
                content = content.replace(this.#getGitHubOpenapiRegexp(commitIds), (match, repository, commitId, openapiFile) => {
                    core.debug(`Match ${match}, Repository ${repository} and CommitId ${commitId}`);
                    if (commitId !== commitIds[repository]) {
                        fileUpdated = true;
                        return `https://raw.githubusercontent.com/${github.context.repo.owner}/${repository}/${commitIds[repository]}/${this.#OPENAPI_FILE_PATH}/${openapiFile}.yaml`;
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