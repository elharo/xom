
## To prepare the release:

Add release notes to 

* website/history.html

Update the version number in

* RELEASING.md (this file)
* README.md
* README.txt
* build.xml
* website/index.html
* website/history.html

Run the reproducible-build verifier:

* `./verify-reproducible.sh`

## To push a new release to Maven Central:

[Generic instructions](https://central.sonatype.org/pages/manual-staging-bundle-creation-and-deployment.html)

1. git checkout master
2. git pull
3. ant clean
4. ant maven2
5. Run `ant bundle` from the repository root. This runs `ant sign` which
  calls `gpg` for each artifact, then assembles `dist/maven2/bundle.zip`.
  If your signing key is not the default GPG key, pass its ID:
  `ant bundle -Dgpg.keyname=YOURKEYID`

6. Login to the [Central Publishing Portal](https://central.sonatype.com/publishing).

7. Select Publish in the upper right hand corner.

8. Click Publish Component

9. Fill in XOM release version as the title and add release notes in the box.

10. Select xom/dist/maven2/bundle.zip and press **Upload Bundle**. If bundle.zip doesn't work, try individual artifacts instead. 

11. If validation succeeds, press the Publish button.

12. Draft the release on GitHub. Code > Tags > Releases > Draft a New Release
  
13. Upload the zip and .tar.gz and other assets to go with the release before publishing it.

14. Publish the release.

## To update the website:

1. sftp the files to IBiblio including the assorted jar and zip files

2. Upload the various binary archives to Github as release assets by editing the latest release on https://github.com/elharo/xom/releases/

3. ant website

4. cd dist/website

5. Upload the site to Dreamhost (add details...)
