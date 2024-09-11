# Update micro-service dependencies action

Bff depends on other microservices.

These dependencies are in the pom and in the openapi files, where there are links to the microservices openapi files.

Each link is composed of the public git-hub path (https://raw.githubusercontent.com), the owner (pagopa), the commitId,
the path to the openapi folder (docs/openapi) and the openapi file name.

Before releasing a new version of the bff, it is a good practice to update those links pointing to the latest released
version (i.e. last tag) of the microservices.

To do this, we have to update the commitId, using that of the last tag.

This action does the work for us. It updates the commitIds of chosen microservices.

## How it works

The action takes as input the list of microservices to update.

For those microservices, it gets the last tag and so the related commitId.

After that it gets the pom version (on the default branch `develop`) and create a branch with
name `update-dependencies/{pomVersion}`. If the branch already exists, it skips the creation step.

Subsequently, it updates the pom and the openapi files.

If there are changes, they are committed and pushed on the newly created branch.

As last step, it opens a pull request or, if it already exists, update it.

The developer has the task of following the instructions indicated in the pull request description and resolving any
conflicts or bugs introduced by the update.

After that the pull request can be merged.

## Inputs

**Required** `token` - the token used to do authenticated request to git-hub\
**Required** `ref` - the branch from where create the pull request branch\
**Required** `pn-auth-fleet` - a flag to set if you want to update the pn-auth-fleet dependency\
**Required** `pn-commons` - a flag to set if you want to update the pn-commons dependency\
**Required** `pn-delivery` - a flag to set if you want to update the pn-delivery dependency\
**Required** `pn-apikey-manager` - a flag to set if you want to update the pn-apikey-manager dependency\
**Required** `pn-external-registries` - a flag to set if you want to update the pn-external-registries dependency\
**Required** `pn-user-attributes` - a flag to set if you want to update the pn-user-attributes dependency\
**Required** `pn-downtime-logs` - a flag to set if you want to update the pn-downtime-logs dependency\
**Required** `pn-delivery-push` - a flag to set if you want to update the pn-delivery-push dependency\
**Required** `pn-mandate` - a flag to set if you want to update the pn-mandate dependency\

## Example usage

```yaml
uses: ./.github/workflows/actions/update-dependencies
with:
  token: ${{ secrets.GITHUB_TOKEN }}
  ref: 'develop'
  pn-auth-fleet: true
  pn-commons: false
  pn-delivery: true
  pn-apikey-manager: false
  pn-external-registries: false
  pn-user-attributes: true
  pn-downtime-logs: false
  pn-delivery-push: false
  pn-mandate: false
```