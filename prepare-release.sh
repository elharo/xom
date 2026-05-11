#!/bin/bash

set -eu

if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <version>" >&2
    echo "Example: $0 1.4.2" >&2
    exit 1
fi

VERSION="$1"
TAG="v$VERSION"
BRANCH="release/$VERSION"
BUILD_FILE="build.xml"

if ! git diff --quiet || ! git diff --cached --quiet; then
    echo "ERROR: Working tree must be clean before preparing a release." >&2
    echo "Commit, stash, or discard local changes and try again." >&2
    exit 1
fi

CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
if [ "$CURRENT_BRANCH" != "master" ] && [ "$CURRENT_BRANCH" != "main" ]; then
    echo "ERROR: Run this script from master or main. Current branch: $CURRENT_BRANCH" >&2
    exit 1
fi

if git rev-parse --verify --quiet "refs/heads/$BRANCH" >/dev/null; then
    echo "ERROR: Branch $BRANCH already exists." >&2
    exit 1
fi

if git rev-parse --verify --quiet "refs/tags/$TAG" >/dev/null; then
    echo "ERROR: Tag $TAG already exists." >&2
    exit 1
fi

git checkout -b "$BRANCH"

TMP_BUILD_FILE="${BUILD_FILE}.tmp"
if ! grep -q '<property name="versionqualifier" value="-SNAPSHOT"/>' "$BUILD_FILE"; then
    echo "ERROR: Expected SNAPSHOT version qualifier property not found in $BUILD_FILE." >&2
    exit 1
fi

sed 's/<property name="versionqualifier" value="-SNAPSHOT"\/>/<property name="versionqualifier" value=""\/>/' "$BUILD_FILE" > "$TMP_BUILD_FILE"
mv "$TMP_BUILD_FILE" "$BUILD_FILE"

if git diff --quiet -- "$BUILD_FILE"; then
    echo "ERROR: Failed to update SNAPSHOT version qualifier in $BUILD_FILE." >&2
    exit 1
fi

git add "$BUILD_FILE"
git commit -m "Prepare $VERSION release"
git tag -a "$TAG" -m "XOM $VERSION"

echo "Release branch and tag created:"
echo "  Branch: $BRANCH"
echo "  Tag:    $TAG"
echo
echo "Next steps:"
echo "  git push origin $BRANCH"
echo "  git push origin $TAG"
echo "  ant clean maven2 bundle"
echo "  Upload dist/maven2/bundle.zip to Maven Central Publishing Portal"
