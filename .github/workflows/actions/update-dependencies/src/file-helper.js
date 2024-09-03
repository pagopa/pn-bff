const core = require('@actions/core')
const fs = require('fs');

function updatePom(path) {
    try {
        const content = fs.readFileSync(path, 'utf8');
        core.info(content);
    } catch (error) {
        throw new Error(`Error reading pom: ${error}`);
    }
}

module.exports = {
    updatePom
}