[Generic instructions](https://central.sonatype.org/pages/manual-staging-bundle-creation-and-deployment.html)

To push a new release to Maven Central:

0. ant clean
1. ant maven2
2. cd dist/maven2
3. Sign the files:

  ```
  $ gpg -ab xom-1.3.1.pom
  $ gpg -ab xom-1.3.1.jar
  $ gpg -ab xom-1.3.1-javadoc.jar
  $ gpg -ab xom-1.3.1-sources.jar
  ```

4. `$ jar -cvf bundle.jar xom-1.3.1.pom xom-1.3.1.pom.asc xom-1.3.1.jar xom-1.3.1.jar.asc xom-1.3.1-javadoc.jar xom-1.3.1-javadoc.jar.asc xom-1.3.1-sources.jar xom-1.3.1-sources.jar.asc`

5. Login to [oss.sonatype.org](https://oss.sonatype.org/#welcome).

6. Select staging upload in the left hand column.

7. Upload Mode: Artifact Bundle

8. Select the bundle.jar and press **Upload Bundle**.

9. Select staging repositories in the left hand side.

10. Scroll to the bottom and find the bundle you just uploaded. Select it.

11. Close the repository. Wait.

12. Release the repository.

If bundle.jar doesn't work, try individual artifacts instead. 
 