[Generic instructions](https://central.sonatype.org/pages/manual-staging-bundle-creation-and-deployment.html)

To push a new release to Maven Central:

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

5. Upload the bundle to Central.

6. Release the bundle

If bundle.jar doesn't work, try individual artifacts instead. 
 