XOM
===

See http://xom.nu/ for more information.

## Compiling XOM

The project uses [Maven](http://maven.apache.org) to manage builds.
Simply type `mvn clean package` to build the project.

Note that as of the time of writing (`2017-02-14`), there may be
test failures and therefore you may need to build the package with
`mvn -DskipTests=true clean package`.

## Site Generation

To generate the XOM website:

```
$ mvn clean package site
```

### Profiles

The project defines a number of optional [build profiles](https://maven.apache.org/guides/introduction/introduction-to-profiles.html)
that allow for the compilation of optional features. These can
be enabled by passing the comma-separated names of the properties
to Maven's `-P` option on the command line:

```
$ mvn -P nu.xom.fat clean package
```

- `nu.xom.fat`: Use "fat" `Text` objects by default. This switches the
  code to an implementation that stores text objects in Java `String`
  types. This yields faster performance at the cost of an increase in
  memory use.

- `nu.xom.lowfat`: Use "low-fat" `Text` objects by default.
  This switches the code to an implementation that stores text objects
  in Java `String` types. This yields faster performance at the cost
  of an increase in memory use. This is the default and cannot be
  specified _in addition_ to `nu.xom.fat`.

- `nu.xom.jdk15`: Include internal classes for pre Java 1.6 compilers.
  This property is enabled if the compiling JDK version is less than
  `1.6`.

- `nu.xom.deployment`: Enable PGP signing of all artifacts produced
  by the build. This profile is enabled automatically if the Java
  system property `nu.xom.deployment` is set. The traditional place
  to do this is via a profile in your `~/.m2/settings.xml`:

```
<?xml version="1.0" encoding="UTF-8"?>
<settings
  xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <profiles>
    <profile>
      <id>xom</id>
      <properties>
        <gpg.useagent>true</gpg.useagent>
        <gpg.keyname>a unique pgp key name</gpg.keyname>
        <nu.xom.deployment>true</nu.xom.deployment>
      </properties>
    </profile>
  </profiles>
</settings>
```

## License

```
XOM is Copyright 2004, 2005, 2009 Elliotte Rusty Harold

   This library is free software; you can redistribute it and/or modify
   it under the terms of version 2.1 of the GNU Lesser General Public
   License as published by the Free Software Foundation.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the
   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
   Boston, MA 02111-1307  USA

You can contact Elliotte Rusty Harold by sending e-mail to
elharo@ibiblio.org. Please include the word "XOM" in the
subject line. For more information see http://www.xom.nu/
or ask a question on the xom-interest mailing list.
```

