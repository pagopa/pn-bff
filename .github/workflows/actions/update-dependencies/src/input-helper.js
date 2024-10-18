const core = require('@actions/core');

class InputHelper {
    static getDependencies() {
        // by default we updated the pn-commons, pn-model and pn-parent dependencies
        // other dependencies are updated based on user choice
        const dependencies = ['pn-commons', 'pn-model', 'pn-parent'];
        const authFleet = core.getInput('pn-auth-fleet', { required: true });
        core.debug(`pn-auth-fleet = ${authFleet}`);
        if (authFleet === "true") {
            dependencies.push('pn-auth-fleet');
        }
        const delivery = core.getInput('pn-delivery', { required: true });
        core.debug(`pn-delivery = ${delivery}`);
        if (delivery === "true") {
            dependencies.push('pn-delivery');
        }
        const apiKeyManager = core.getInput('pn-apikey-manager', { required: true });
        core.debug(`pn-apikey-manager = ${apiKeyManager}`);
        if (apiKeyManager === "true") {
            dependencies.push('pn-apikey-manager');
        }
        const externalRegistries = core.getInput('pn-external-registries', { required: true });
        core.debug(`pn-external-registries = ${externalRegistries}`);
        if (externalRegistries === "true") {
            dependencies.push('pn-external-registries');
        }
        const userAttributes = core.getInput('pn-user-attributes', { required: true });
        core.debug(`pn-user-attributes = ${userAttributes}`);
        if (userAttributes === "true") {
            dependencies.push('pn-user-attributes');
        }
        const downtimeLogs = core.getInput('pn-downtime-logs', { required: true });
        core.debug(`pn-downtime-logs = ${downtimeLogs}`);
        if (downtimeLogs === "true") {
            dependencies.push('pn-downtime-logs');
        }
        const deliveryPush = core.getInput('pn-delivery-push', { required: true });
        core.debug(`pn-delivery-push = ${deliveryPush}`);
        if (deliveryPush === "true") {
            dependencies.push('pn-delivery-push');
        }
        const mandate = core.getInput('pn-mandate', { required: true });
        core.debug(`pn-mandate = ${mandate}`);
        if (mandate === "true") {
            dependencies.push('pn-mandate');
        }
        return dependencies;
    }

    static getEnvVariable(name) {
      const val = process.env[name.toUpperCase()] || ''
      return val.trim()
    }
}

module.exports = {
    InputHelper
}