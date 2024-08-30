const core = require('@actions/core');

function getDependencies() {
    const dependencies = [];
    const authFleet = core.getInput('pn-auth-fleet');
    core.debug(`pn-auth-fleet = ${authFleet}`);
    if (authFleet) {
        dependencies.push('pn-auth-fleet');
    }
    const commons = core.getInput('pn-commons');
    core.debug(`pn-commons = ${commons}`);
    if (commons) {
        dependencies.push('pn-commons');
    }
    const delivery = core.getInput('pn-delivery');
    core.debug(`pn-delivery = ${delivery}`);
    if (delivery) {
        dependencies.push('pn-delivery');
    }
    const apiKeyManager = core.getInput('pn-apikey-manager');
    core.debug(`pn-apikey-manager = ${apiKeyManager}`);
    if (apiKeyManager) {
        dependencies.push('pn-apikey-manager');
    }
    return dependencies;
}

module.exports = {
    getInputs
}