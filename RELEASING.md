
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
   * optionally runs `ant bundle` if the GPG secrets are configured
   * tags the release
   * creates `prepare-${nextVersion}-snapshot` from `master`
   * updates `build.xml` on that branch to `${nextVersion}-SNAPSHOT`
   * pushes the release branch, the snapshot-preparation branch, and the tag
   * opens a pull request from `prepare-${nextVersion}-snapshot` back to
     `master` so the protected branch can be reviewed before it advances
   * creates the GitHub release and uploads the built archives

5. Run the reproducible-build verifier if you want an extra local check:

* `./verify-reproducible.sh`

## To push a new release to Maven Central:

[Generic instructions](https://central.sonatype.org/pages/manual-staging-bundle-creation-and-deployment.html)

1. Check out the tagged release commit or release branch.

2. If the workflow created `dist/maven2/bundle.zip`, use that. Otherwise run
   `ant bundle` from the repository root. This runs `ant sign` which calls
   `gpg` for each artifact, then assembles `dist/maven2/bundle.zip`. If your
   signing key is not the default GPG key, pass its ID:
   `ant bundle -Dgpg.keyname=YOURKEYID`

3. Login to the [Central Publishing Portal](https://central.sonatype.com/publishing).

4. Select Publish in the upper right hand corner.

5. Click Publish Component

6. Fill in XOM release version as the title and add release notes in the box.

7. Select `xom/dist/maven2/bundle.zip` and press **Upload Bundle**. If
   `bundle.zip` doesn't work, try individual artifacts instead.

8. If validation succeeds, press the Publish button.

9. `ant dist`

10. If needed, edit the GitHub release that the workflow created and attach any
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

5. Upload the site to Dreamhost (add details...)
