## One-time repository setup:

### Create a dedicated release signing key

Use a key that is only for XOM releases (not your personal daily-use key):

```bash
gpg --full-generate-key
```

When prompted, choose:

* key type: `RSA and RSA`
* key size: `4096`
* expiration: your team policy (no expiration is acceptable if that is your
  policy)
* user ID: release-maintainer identity for XOM

List keys and copy the primary key fingerprint (40 hex characters):

```bash
gpg --list-secret-keys --keyid-format LONG
```

Publish the public key so Maven Central can verify signatures:

```bash
gpg --keyserver keys.openpgp.org --send-keys <KEY_FINGERPRINT>
```

Export the private key in ASCII-armored form for GitHub Actions:

```bash
gpg --armor --export-secret-keys <KEY_FINGERPRINT>
```

Copy the complete output, including:

* `-----BEGIN PGP PRIVATE KEY BLOCK-----`
* `-----END PGP PRIVATE KEY BLOCK-----`

### Configure GitHub repository secrets

In GitHub, open:
**Settings → Secrets and variables → Actions → Repository secrets**

Add these secrets:

* `GPG_PRIVATE_KEY` (**required**): full ASCII-armored private key exported above
* `GPG_PASSPHRASE` (**required**): passphrase for that signing key
* `RELEASE_TOKEN` (**optional**): PAT with Contents: write; used instead of
  `GITHUB_TOKEN` when branch protection blocks default token pushes

If either required GPG secret is missing, the release workflow fails during key
import/signing and does not create a release.

## To prepare the release:

1. Before running the release workflow, update `master` with the release notes
   in `website/history.html`.

2. Run `./prepare-release.sh X.Y.Z` (for example
   `./prepare-release.sh 1.4.2`) from `master` to update `build.xml`,
   `README.md`, `README.txt`, `website/index.html`, `website/sidebar.html`, and
   `src/nu/xom/Info.java` to the published release version. The script
   creates a `prepare-release-X.Y.Z` branch, commits the changes, pushes
   the branch, and opens a pull request.

3. Review and merge the pull request opened by `./prepare-release.sh` so
   that `master` contains the release-versioned files before the workflow
   runs.

4. Run the **Release** GitHub Actions workflow from `master` and pass both the
   release version (for example `1.4.2`) and the next development version (for
   example `1.4.3`).

   The workflow automatically:

   * verifies that `build.xml`, `README.md`, `README.txt`,
     `website/index.html`, `website/sidebar.html`, and `src/nu/xom/Info.java` on `master`
     match the requested release version
   * creates the `release-${releaseVersion}` branch from `master`
   * runs `ant dist`
   * imports the configured GPG private key and runs `ant bundle`
   * tags the release
   * creates `prepare-${nextVersion}-snapshot` from `master`
   * updates `build.xml` on that branch to `${nextVersion}-SNAPSHOT`
   * pushes the release branch, the snapshot-preparation branch, and the tag
   * opens a pull request from `prepare-${nextVersion}-snapshot` back to
     `master` so the protected branch can be reviewed before it advances
   * uploads `dist/maven/bundle.zip` to the workflow run artifacts as
     `maven-central-bundle-${releaseVersion}`
   * creates the GitHub release only after `dist/maven2/bundle.zip` is created
     and uploads the built archives (including `bundle.zip`)

5. Run the reproducible-build verifier if you want an extra local check:

* `./verify-reproducible.sh`

## To push a new release to Maven Central:

[Generic instructions](https://central.sonatype.org/pages/manual-staging-bundle-creation-and-deployment.html)

1. Download `maven-central-bundle-X.Y.Z` from the **Artifacts** section of the
   successful release workflow run and use the included `bundle.zip`.

2. Login to the [Central Publishing Portal](https://central.sonatype.com/publishing).

3. Select Publish in the upper right hand corner.

4. Click Publish Component

5. Fill in XOM release version as the title and add release notes in the box.

6. Locate `maven-central-bundle-X.Y.Z` and press **Upload Bundle**.

7. If validation succeeds, press the Publish button.

8. If needed, edit the GitHub release that the workflow created and attach any
    additional assets before publishing it.

## If the release fails before Maven Central publishes it:

Treat the GitHub release, tag, and `prepare-${nextVersion}-snapshot` pull
request as provisional until Maven Central has published the artifacts. Do not
merge the snapshot-preparation pull request until Maven Central publication has
succeeded.

If Central validation, upload, or publishing fails in a way that requires code
changes, do not burn the version. Instead:

1. Leave `master` on the release version. If the snapshot-preparation pull
   request is open, keep it unmerged. If it was merged by mistake, revert it so
   `master` is back on the release version before recutting.

2. Delete the provisional GitHub release and delete tag `vX.Y.Z`.

3. Delete branch `release-X.Y.Z`.

4. Close the `prepare-${nextVersion}-snapshot` pull request and delete branch
   `prepare-${nextVersion}-snapshot`.

5. Make the required fixes on `master`, keeping the intended release version in
   `website/history.html`.

6. Re-run `./prepare-release.sh X.Y.Z` from `master` so the release-versioned
   files and `build.modtime` are refreshed for the recut. Review and merge
   the pull request it opens before proceeding.

7. Run the **Release** workflow again with the same `releaseVersion` and
   `nextVersion`.

Only after Maven Central has published the artifacts should the release be
treated as final and the `prepare-${nextVersion}-snapshot` pull request be
merged.

## To update the website:

1. sftp the files to IBiblio including the assorted jar and zip files

2. Upload the various binary archives to Github as release assets by editing the latest release on https://github.com/elharo/xom/releases/

3. ant website

4. cd dist/website

5. Upload the site from dist/website (not build/website!) to Dreamhost using sftp or scp.
