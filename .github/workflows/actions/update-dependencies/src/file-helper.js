const core = require('@actions/core');
const github = require('@actions/github');
const fs = require('fs');
const xml2js = require('xml2js');

const {InputHelper} = require('./input-helper');

class FileHelper {

    #POM_PATH = './pom.xml';
    #OPENAPI_ROOT_PATH = 'https:\\raw.githubusercontent.com';
    #OPENAPI_FILE_PATH = 'docs/openapi';
    #OPENAPI_DIRECTORY_PATH = './docs/openapi';

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

     updatePom(commitIds) {
         core.info('Updating POM');
         core.debug(`Reading pom at path ${this.#POM_PATH}`);
         try {
             core.debug(`Reading POM content`);
             // read content
             let content = fs.readFileSync(this.#POM_PATH, 'utf8');
             core.debug(`Updating POM content`);
             // change content
             let pomUpdated = false;
             content = content.replace(this.#getGitHubOpenapiRegexp(commitIds), (match, repository, commitId, openapiFile) => {
                 core.debug(`Match ${match}`);
                 core.debug(`Repository ${repository}`);
                 core.debug(`CommitId ${commitId}`);
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
         const name = `${dirPath}/${file}`
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
        core.debug(`Reading openapi files at path ${this.#OPENAPI_DIRECTORY_PATH}`);
        const files = this.#getFilesInADirectory(this.#OPENAPI_DIRECTORY_PATH, ['aws', 'api-external']);
        core.info(`Files found at path ${this.#OPENAPI_DIRECTORY_PATH}: ${files.join(', ')}`)
     }
}

module.exports = {
    FileHelper
}