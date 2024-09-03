const core = require('@actions/core')
const fs = require('fs');

function async updatePom(path) {
    try {
        const content = await fs.readFile(path, 'utf8');
        core.inf(content);
    } catch (error) {
        throw new Error(`Error reading pom: ${error}`);
    }
}

module.exports = {
    updatePom
}