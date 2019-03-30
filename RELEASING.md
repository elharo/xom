[Generic instructions](https://central.sonatype.org/pages/manual-staging-bundle-creation-and-deployment.html)

To push a new release to Maven Central:

1. git checkout master
2. git pull
3. ant clean
5. ant maven2
6. cd dist/maven2
7. Sign the files:

  ```
  $ gpg -ab xom-1.3.2.pom
  $ gpg -ab xom-1.3.2.jar
  $ gpg -ab xom-1.3.2-javadoc.jar
  $ gpg -ab xom-1.3.2-sources.jar
  ```

8. `$ jar -cvf bundle.jar xom-1.3.2.pom xom-1.3.2.pom.asc xom-1.3.2.jar xom-1.3.2.jar.asc xom-1.3.2-javadoc.jar xom-1.3.2-javadoc.jar.asc xom-1.3.2-sources.jar xom-1.3.2-sources.jar.asc`

9. Login to [oss.sonatype.org](https://oss.sonatype.org/#welcome).

10. Select staging upload in the left hand column.

11. Upload Mode: Artifact Bundle

12. Select the bundle.jar and press **Upload Bundle**. If bundle.jar doesn't work, try individual artifacts instead. 

13. Select staging repositories in the left hand side.

14. Scroll to the bottom and find the bundle you just uploaded. Select it.

15. Close the repository. Wait.

16. Release the repository.

17. Tag the release on github.


 