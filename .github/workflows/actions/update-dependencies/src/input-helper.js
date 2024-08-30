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
    const externalRegistries = core.getInput('pn-external-registries');
    core.debug(`pn-external-registries = ${externalRegistries}`);
    if (externalRegistries) {
        dependencies.push('pn-external-registries');
    }
    const userAttributes = core.getInput('pn-user-attributes');
    core.debug(`pn-user-attributes = ${userAttributes}`);
    if (userAttributes) {
        dependencies.push('pn-user-attributes');
    }
    const downtimeLogs = core.getInput('pn-downtime-logs');
    core.debug(`pn-downtime-logs = ${downtimeLogs}`);
    if (downtimeLogs) {
        dependencies.push('pn-downtime-logs');
    }
    const deliveryPush = core.getInput('pn-delivery-push');
    core.debug(`pn-delivery-push = ${deliveryPush}`);
    if (deliveryPush) {
        dependencies.push('pn-delivery-push');
    }
    const mandate = core.getInput('pn-mandate');
    core.debug(`pn-mandate = ${mandate}`);
    if (mandate) {
        dependencies.push('pn-mandate');
    }
    return dependencies;
}

module.exports = {
    getDependencies
}