#!/bin/bash
# Verify that the XOM build is reproducible.
#
# This script builds xom.jar twice with the same SOURCE_DATE_EPOCH and
# compares the results. If the builds are identical, the build is
# reproducible. If not, it exits with a non-zero status code.
#
# Usage:
#   ./verify-reproducible.sh [SOURCE_DATE_EPOCH]
#
# If SOURCE_DATE_EPOCH is not provided, the script uses the timestamp
# of the most recent git tag, or 0 if no tags are found.
#
# For release builds, pass the epoch of the release date, e.g.:
#   SOURCE_DATE_EPOCH=1700000000 ./verify-reproducible.sh

set -e

# Determine SOURCE_DATE_EPOCH
if [ -n "$1" ]; then
    EPOCH="$1"
elif [ -n "$SOURCE_DATE_EPOCH" ]; then
    EPOCH="$SOURCE_DATE_EPOCH"
else
    # Use the timestamp of the latest git tag, or 0 if none
    LATEST_TAG=$(git describe --tags --abbrev=0 2>/dev/null || echo "")
    if [ -n "$LATEST_TAG" ]; then
        EPOCH=$(git log -1 --format=%ct "$LATEST_TAG" 2>/dev/null || echo "0")
    else
        EPOCH=0
    fi
fi

export SOURCE_DATE_EPOCH="$EPOCH"
export TZ=UTC

echo "Verifying reproducible build with SOURCE_DATE_EPOCH=${SOURCE_DATE_EPOCH}"

TMPDIR=$(mktemp -d)
trap 'rm -rf "$TMPDIR"' EXIT

# First build
echo "--- First build ---"
ant clean jar
cp build/xom-*.jar "$TMPDIR/build1.jar"

# Second build (clean)
echo "--- Second build ---"
ant clean jar
cp build/xom-*.jar "$TMPDIR/build2.jar"

# Compare
echo "--- Comparing builds ---"
if cmp -s "$TMPDIR/build1.jar" "$TMPDIR/build2.jar"; then
    echo "SUCCESS: Builds are identical. The build is reproducible."
    sha256sum "$TMPDIR/build1.jar"
else
    echo "FAILURE: Builds differ. The build is NOT reproducible."
    sha256sum "$TMPDIR/build1.jar" "$TMPDIR/build2.jar"
    exit 1
fi
