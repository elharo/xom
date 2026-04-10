#!/bin/bash
# Verify that the XOM build is reproducible.
#
# This script builds xom.jar twice and compares the results. If the builds
# are identical, the build is reproducible. If not, it exits with a non-zero
# status code.
#
# Usage:
#   ./verify-reproducible.sh

set -e

# Verify required commands are available
for cmd in ant cp cmp sha256sum mktemp; do
    if ! command -v "$cmd" >/dev/null 2>&1; then
        echo "ERROR: Required command not found: $cmd" >&2
        exit 1
    fi
done

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
