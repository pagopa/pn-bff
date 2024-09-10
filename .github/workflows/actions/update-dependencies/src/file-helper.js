const core = require('@actions/core');
const github = require('@actions/github');
const fs = require('fs');
const xml2js = require('xml2js');

const {InputHelper} = require('./input-helper');

class FileHelper {

    #repositoryHelper;
    #POM_PATH = './pom.xml';
    #OPENAPI_ROOT_PATH = 'https://raw.githubusercontent.com';
    #OPENAPI_FILE_PATH = 'docs/openapi';

    constructor(repositoryHelper) {
        this.#repositoryHelper = repositoryHelper;
    }

    #getGitHubOpenapiRegexp(commitIds) {
         const dependencies = Object.keys(commitIds);
         const regexp = new RegExp(`${this.#OPENAPI_ROOT_PATH}/${github.context.repo.owner}/(?<repository>${dependencies.join('|')})/(?<commitId>.+)/${this.#OPENAPI_FILE_PATH}/(?<openapiFile>.+).yaml`, 'g');
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

     async updatePom(branchName, commitIds) {
         core.info('Updating POM');
         core.debug(`Reading pom at path ${this.#POM_PATH}`);
         try {
             core.debug(`Reading POM content`);
             // read content
             await this.#repositoryHelper.getFileContent(branchName, 'pom.xml');
             let content = fs.readFileSync(this.#POM_PATH, 'utf8');
             core.debug(`Updating POM content`);
             // change content
             let pomUpdated = false;
             content = content.replace(this.#getGitHubOpenapiRegexp(commitIds), (match, repository, commitId, openapiFile) => {
                 core.debug(`Match ${match}, Repository ${repository} and CommitId ${commitId}`);
                 if (commitId !== commitIds[repository]) {
                    pomUpdated = true;
                    return `${this.#OPENAPI_ROOT_PATH}/${github.context.repo.owner}/${repository}/${commitIds[repository]}/${this.#OPENAPI_FILE_PATH}/${openapiFile}.yaml`;
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

     // Recursive function to get files
     #getFilesInADirectory(dirPath, excluded = []) {
       let files = [];
       // Get an array of all files and directories in the passed directory using fs.readdirSync
       const fileList = fs.readdirSync(dirPath);
       // Create the full path of the file/directory by concatenating the passed directory and file/directory name
       for (const file of fileList) {
         const name = `${dirPath}/${file}`;
         if (excluded.some(excl => name.includes(excl))) {
            continue;
         }
         // Check if the current file/directory is a directory using fs.statSync
         if (fs.statSync(name).isDirectory()) {
           // If it is a directory, recursively call the getFiles function with the directory path and the files array
           files = files.concat(this.#getFilesInADirectory(name, excluded));
           continue;
         }
         // If it is a file, push the full path to the files array
         files.push(name);
       }
       return files;
     }

     updateOpenapi(commitIds) {
        core.info('Updating openapi files');
        core.debug(`Reading openapi files at path ./${this.#OPENAPI_FILE_PATH}`);
        const files = this.#getFilesInADirectory(`./${this.#OPENAPI_FILE_PATH}`, ['aws', 'api-external']);
        core.debug(`Files found at path ./${this.#OPENAPI_FILE_PATH}: ${files.join(', ')}`);
        if (files.length === 0) {
            core.info('No openapi file to update');
            return;
        }
        const filesToUpdate = [];
        for (const file of files) {
            try {
                core.info(`Updating openapi file at path ${file}`);
                // read content
                let content = fs.readFileSync(file, 'utf8');
                // change content
                let fileUpdated = false;
                content = content.replace(this.#getGitHubOpenapiRegexp(commitIds), (match, repository, commitId, openapiFile) => {
                    core.debug(`Match ${match}, Repository ${repository} and CommitId ${commitId}`);
                    if (commitId !== commitIds[repository]) {
                        fileUpdated = true;
                        return `${this.#OPENAPI_ROOT_PATH}/${github.context.repo.owner}/${repository}/${commitIds[repository]}/${this.#OPENAPI_FILE_PATH}/${openapiFile}.yaml`;
                    }
                    return match;
                });
                if (fileUpdated) {
                    core.info(`Openapi file at path ${file} updated successfully`);
                    filesToUpdate.push({path: file.replace('./', ''), content})
                    continue;
                }
                core.info(`Openapi file at path ${file} already updated`);
            } catch (error) {
                throw new Error(`Error reading openapi file at path ${file}: ${error}`);
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