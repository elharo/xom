#!/usr/bin/env bash

set -e

usage() {
    echo "Usage: $0 [--check] X.Y.Z" >&2
    exit 1
}

CHECK_ONLY=false

if [ "$#" -eq 2 ] && [ "$1" = "--check" ]; then
    CHECK_ONLY=true
    RELEASE_VERSION="$2"
elif [ "$#" -eq 1 ]; then
    RELEASE_VERSION="$1"
else
    usage
fi

export RELEASE_VERSION
export CHECK_ONLY

python - <<'PY'
from datetime import datetime, timezone
import os
import re
import tempfile
from pathlib import Path

release = os.environ["RELEASE_VERSION"]
check_only = os.environ["CHECK_ONLY"] == "true"

if not re.match(r"^\d+\.\d+\.\d+$", release):
    raise SystemExit("Release version must look like X.Y.Z")

major, minor, micro = release.split(".")
timestamp = None
if not check_only:
    timestamp = datetime.now(timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ")


def replace(path, replacements):
    path = Path(path)
    text = path.read_text(encoding="utf-8")
    for pattern, repl in replacements:
        text, count = re.subn(pattern, repl, text, flags=re.MULTILINE)
        if count < 1:
            raise SystemExit(
                f"Pattern {pattern} matched 0 times in {path} "
                f"(expected at least 1 match)"
            )
    temp_file = tempfile.NamedTemporaryFile(dir=str(path.parent), delete=False)
    temp_path = temp_file.name
    temp_file.close()
    try:
        Path(temp_path).write_text(text, encoding="utf-8")
        Path(temp_path).replace(path)
    except Exception:
        try:
            if Path(temp_path).exists():
                Path(temp_path).unlink()
        except Exception:
            pass
        raise


def assert_matches(path, patterns):
    text = Path(path).read_text(encoding="utf-8")
    for pattern in patterns:
        if not re.search(pattern, text, re.MULTILINE):
            raise SystemExit(f"Update {path} for release {release} before continuing.")


if check_only:
    assert_matches("build.xml", [
        r'<property name="build\.modtime" value="[^"]+"/>',
        r'<property name="majorversion" value="' + re.escape(major) + r'"/>',
        r'<property name="minorversion" value="' + re.escape(minor) + r'"/>',
        r'<property name="microversion" value="' + re.escape(micro) + r'"/>',
        r'<property name="versionqualifier" value=""/>',
    ])

    assert_matches("README.md", [
        r'The current version of XOM is ' + re.escape(release),
        r'<version>' + re.escape(release) + r'</version>',
        r"compile 'xom:xom:" + re.escape(release) + r"'",
        r'upgrade to ' + re.escape(release),
    ])

    assert_matches("README.txt", [
        r'<version>' + re.escape(release) + r'</version>',
        r"implementation 'xom:xom:" + re.escape(release) + r"'",
        r'xom-' + re.escape(release) + r'\.jar',
    ])

    assert_matches("website/index.html", [
        r'xom-' + re.escape(release) + r'\.jar',
        r'xom-' + re.escape(release) + r'\.zip',
        r'xom-' + re.escape(release) + r'\.tar\.gz',
        r'releases/download/v' + re.escape(release) + r'/xom-' + re.escape(release) + r'-src\.zip',
        r'releases/download/v' + re.escape(release) + r'/xom-' + re.escape(release) + r'-src\.tar\.gz',
        r'&lt;version&gt;' + re.escape(release) + r'&lt;/version&gt;',
        r"compile 'xom:xom:" + re.escape(release) + r"'",
        r'The current version of XOM is ' + re.escape(release),
        r'upgrade to ' + re.escape(release),
    ])

    assert_matches("src/nu/xom/Info.java", [
        r'@version ' + re.escape(release),
        r'"' + re.escape(release) + r' or later"',
    ])
else:
    replace("build.xml", [
        (r'(<property name="build\.modtime" value=")[^"]+("/>)', r'\g<1>' + timestamp + r'\g<2>'),
        (r'(<property name="majorversion" value=")\d+("/>)', r'\g<1>' + major + r'\g<2>'),
        (r'(<property name="minorversion" value=")\d+("/>)', r'\g<1>' + minor + r'\g<2>'),
        (r'(<property name="microversion" value=")\d+("/>)', r'\g<1>' + micro + r'\g<2>'),
        (r'(<property name="versionqualifier" value=")[^"]*("/>)', r'\g<1>\g<2>'),
    ])

    replace("README.md", [
        (r'The current version of XOM is [0-9.]+', 'The current version of XOM is ' + release),
        (r'<version>[0-9.]+</version>', '<version>' + release + '</version>'),
        (r"compile 'xom:xom:[^']+'", "compile 'xom:xom:" + release + "'"),
        (r'upgrade to [0-9.]+', 'upgrade to ' + release),
    ])

    replace("README.txt", [
        (r'<version>[0-9.]+</version>', '<version>' + release + '</version>'),
        (r"implementation 'xom:xom:[^']+'", "implementation 'xom:xom:" + release + "'"),
        (r'xom-[0-9.]+\.jar', 'xom-' + release + '.jar'),
    ])

    replace("website/index.html", [
        (r'xom-[0-9.]+\.jar', 'xom-' + release + '.jar'),
        (r'xom-[0-9.]+\.zip', 'xom-' + release + '.zip'),
        (r'xom-[0-9.]+\.tar\.gz', 'xom-' + release + '.tar.gz'),
        (r'releases/download/v[0-9.]+/xom-[0-9.]+-src\.zip', 'releases/download/v' + release + '/xom-' + release + '-src.zip'),
        (r'releases/download/v[0-9.]+/xom-[0-9.]+-src\.tar\.gz', 'releases/download/v' + release + '/xom-' + release + '-src.tar.gz'),
        (r'&lt;version&gt;[0-9.]+&lt;/version&gt;', '&lt;version&gt;' + release + '&lt;/version&gt;'),
        (r"compile 'xom:xom:[^']+'", "compile 'xom:xom:" + release + "'"),
        (r'The current version of XOM is [0-9.]+', 'The current version of XOM is ' + release),
        (r'upgrade to [0-9.]+', 'upgrade to ' + release),
    ])

    replace("src/nu/xom/Info.java", [
        (r'@version [0-9.]+', '@version ' + release),
        (r'"[0-9.]+ or later"', '"' + release + ' or later"'),
    ])
PY

if [ "$CHECK_ONLY" = "true" ]; then
    echo "Release files already match $RELEASE_VERSION"
else
    echo "Updated release files for $RELEASE_VERSION"
    PREPARE_BRANCH="prepare-release-${RELEASE_VERSION}"
    if git show-ref --verify --quiet "refs/heads/${PREPARE_BRANCH}"; then
        echo "Local branch ${PREPARE_BRANCH} already exists." >&2
        exit 1
    fi
    if git ls-remote --exit-code --heads origin "${PREPARE_BRANCH}" >/dev/null 2>&1; then
        echo "Remote branch ${PREPARE_BRANCH} already exists." >&2
        exit 1
    fi
    git switch -c "${PREPARE_BRANCH}"
    git add build.xml README.md README.txt website/index.html src/nu/xom/Info.java
    git commit -m "Prepare release ${RELEASE_VERSION}"
    git push origin "${PREPARE_BRANCH}"
    gh pr create \
        --base master \
        --head "${PREPARE_BRANCH}" \
        --title "Prepare release ${RELEASE_VERSION}" \
        --body "Updates build.xml, README.md, README.txt, website/index.html, and src/nu/xom/Info.java to version ${RELEASE_VERSION} in preparation for the release workflow."
    echo "Opened pull request for ${PREPARE_BRANCH}"
fi
