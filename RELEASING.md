
## To prepare the release:

1. Make sure `master` is on the next `-SNAPSHOT` version in `build.xml`.

2. Create a release branch from `master`.

3. Add release notes to

* website/history.html

4. Run the **Release** GitHub Actions workflow from that release branch and
   pass the release version (for example `1.4.2`).

   The workflow automatically:

   * updates `build.xml`, `README.md`, `README.txt`, `website/index.html`,
     and `src/nu/xom/Info.java` to the release version
   * updates `build.modtime`
   * runs `ant dist`
   * optionally runs `ant bundle` if the GPG secrets are configured
   * commits the release version on the release branch
   * tags the release
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

## To update the website:

1. sftp the files to IBiblio including the assorted jar and zip files

2. Upload the various binary archives to Github as release assets by editing the latest release on https://github.com/elharo/xom/releases/

3. ant website

4. cd dist/website

5. Upload the site to Dreamhost (add details...)
