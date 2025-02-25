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

For all microservices in `src/dependencies.js`, it gets the last tag and so the related commitId.

After that it gets the pom version (on the default branch `develop`) and create a branch with
name `update-dependencies/{pomVersion}`. If the branch already exists, it skips the creation step.

Subsequently, it updates the pom and the openapi files.

If there are changes, they are committed and pushed on the newly created branch.

As last step, it opens a pull request or, if it already exists, update it.

The developer has the task of following the instructions indicated in the pull request description and resolving any
conflicts or bugs introduced by the update.

After that the pull request can be merged.

## Example usage

```yaml
uses: ./.github/workflows/actions/update-dependencies
with:
  token: ${{ secrets.GITHUB_TOKEN }}
  ref: 'develop'
```