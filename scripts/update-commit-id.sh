#!/bin/bash -e

# Update commit ID of a pn-* repository in all remote references
# (inputSpec in pom.xml and $ref under docs/openapi).
# Excludes generated files: api-external-*.yaml and everything under docs/openapi/aws
#
# Usage: ./scripts/update-commit-id.sh <repo> <commit-id>
# Example: ./scripts/update-commit-id.sh pn-delivery 320a2a887ad101f920a299164078b536fe1fb3e5

repo=$1
sha=$2

if [[ -z $repo || -z $sha ]]; then
    echo "Usage: $0 <repo> <commit-id>"
    exit 1
fi

if [[ ! $repo =~ ^pn-[a-z-]+$ ]]; then
    echo "Invalid repository name: ${repo}"
    exit 1
fi

if [[ ! $sha =~ ^[0-9a-f]{40}$ ]]; then
    echo "Invalid commit ID: ${sha} (expected 40 hexadecimal characters)"
    exit 1
fi

cd "$(dirname "$0")/.."

echo "Searching for files containing references to ${repo}..."

files=$(
    {
        echo pom.xml
        find docs/openapi -type f -name '*.yaml' \
            ! -name 'api-external-*.yaml' \
            ! -path 'docs/openapi/aws/*'
    } | sort
)

# "pagopa/<repo>/<sha>": the slash after the name prevents pn-delivery from matching pn-delivery-push
pattern="pagopa/${repo}/[0-9a-f]{40}"

old_shas=$(echo "$files" | xargs grep -hoE "$pattern" | sort -u | awk -F/ '{print $3}' | grep -v "^${sha}$" || true)

if [[ -z $old_shas ]]; then
    echo "No references to ${repo} need updating (already at ${sha} or not present)"
else
    echo "Current commits for ${repo}:"
    echo "$old_shas" | sed 's/^/  /'
    echo "New commit: ${sha}"
    echo

    for f in $files; do
        count=$(grep -oE "$pattern" "$f" | grep -vc "/${sha}$" || true)
        if [[ $count -gt 0 ]]; then
            perl -pi -e "s#pagopa/${repo}/[0-9a-f]{40}#pagopa/${repo}/${sha}#g" "$f"
            echo "  Updated ${f}: ${count} occurrence(s)"
        fi
    done

    echo
    echo "Update completed successfully!"
fi
echo

# Final check: each repository should have only one commit ID
echo "Performing final consistency check..."
mixed=$(echo "$files" | xargs grep -hoE "pagopa/pn-[a-z-]+/[0-9a-f]{40}" | sort -u | awk -F/ '{print $2}' | uniq -d)

if [[ -n $mixed ]]; then
    echo "WARNING: repositories with different commit IDs across files:"
    for r in $mixed; do
        echo "  ${r}:"
        echo "$files" | xargs grep -hoE "pagopa/${r}/[0-9a-f]{40}" | sort | uniq -c | sed 's/^/   /'
    done
    exit 1
fi

echo "SUCCESS: All repositories have consistent commit IDs"