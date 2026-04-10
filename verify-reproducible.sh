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
# If SOURCE_DATE_EPOCH is not provided (neither as an argument nor as an
# environment variable), a fixed sentinel value of 1234567890 is used.
# This value (2009-02-13T23:31:30Z) is chosen as a well-known, memorable
# timestamp ("Unix time billion") that is clearly artificial and distinct
# from any real release date.
#
# For release builds, pass the epoch of the release date, e.g.:
#   SOURCE_DATE_EPOCH=1700000000 ./verify-reproducible.sh

# Fixed sentinel epoch used when no SOURCE_DATE_EPOCH is supplied.
DEFAULT_EPOCH=1234567890

set -e

# Verify required commands are available
for cmd in ant cp cmp sha256sum mktemp; do
    if ! command -v "$cmd" >/dev/null 2>&1; then
        echo "ERROR: Required command not found: $cmd" >&2
        exit 1
    fi
done

# Determine SOURCE_DATE_EPOCH
if [ -n "$1" ]; then
    EPOCH="$1"
elif [ -n "$SOURCE_DATE_EPOCH" ]; then
    EPOCH="$SOURCE_DATE_EPOCH"
else
    EPOCH="$DEFAULT_EPOCH"
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
