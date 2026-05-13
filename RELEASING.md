
## To prepare the release:

1. Before running the release workflow, update `master` with the release notes
   in `website/history.html` and with the release version in `README.md`,
   `README.txt`, `website/index.html`, and `src/nu/xom/Info.java` so the
   workflow cuts the release branch from the already-prepared published
   version.

2. Run the **Release** GitHub Actions workflow from `master` and pass both the
   release version (for example `1.4.2`) and the next development version (for
   example `1.4.3`).

   The workflow automatically:

   * verifies that `README.md`, `README.txt`, `website/index.html`, and
     `src/nu/xom/Info.java` on `master` already match the requested release
     version
   * creates the `release-${releaseVersion}` branch from `master`
   * updates `build.xml` on that branch to the release version
   * updates `build.modtime`
   * runs `ant dist`
   * optionally runs `ant bundle` if the GPG secrets are configured
   * commits the release version on the release branch
   * tags the release
   * creates `prepare-${nextVersion}-snapshot` from `master`
   * updates `build.xml` on that branch to `${nextVersion}-SNAPSHOT`
   * pushes the release branch, the snapshot-preparation branch, and the tag
   * opens a pull request from `prepare-${nextVersion}-snapshot` back to
     `master` so the protected branch can be reviewed before it advances
   * creates the GitHub release and uploads the built archives

3. Run the reproducible-build verifier if you want an extra local check:

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

## To update the website:

1. sftp the files to IBiblio including the assorted jar and zip files

2. Upload the various binary archives to Github as release assets by editing the latest release on https://github.com/elharo/xom/releases/

3. ant website

4. cd dist/website

5. Upload the site to Dreamhost (add details...)
