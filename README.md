# Nextversion maven plugin

![Java CI](https://github.com/andreasaronsson/nextversion-maven-plugin/workflows/Java%20CI/badge.svg)

[![Download](https://api.bintray.com/packages/aron/maven/nextversion-maven-plugin/images/download.svg)](https://bintray.com/aron/maven/nextversion-maven-plugin/_latestVersion)

This plugin can be used to release.
Increments versions CD style.
Java 11+.
Queries the deployment repo to know 'previous version'.
Then increments the version number.
Changes in pom.xml are not checked in.

Verified with Jenkins and Nexus.

## How versions are incremented

When the version number has three segments <https://semver.org>.
Increments the patch part of the project version before the build begins.
This means that `1.0.0` is changed to `1.0.1` and `2.0.99` is changed to `2.0.100` and so on.

With two digits the minor number is incremented.
`5.3`will be changed to `5.4`.

When there is only one segment this number is incremented.
This means that `1` is changed to `2`.

The version present in the pom file is used as fallback.
If there is no released version the plugin will use the version in the pom.
An existing -SNAPSHOT substring will be removed.

If you wish to increment a major or minor number.
It is possible to set a newer version.
The plugin will count upwards from that.

*Given* latest release version is `1.59`.
*When* version `2.0-SNAPSHOT` is set in the pom.
*Then* nextversion-maven-plugin will use version `2.0`.

## Traceability

Two properties are added to the build context.
The git sha checksum `nextversion.commit`.
The version number `nextversion.version`.
Set as maven system properties `${nextversion.commit}` and `${nextversion.version}`.
These values are intended to be used in the jar MANIFEST.MF or the like.
They are also added to the deployed pom file.
Both properties are written to `target/nextversion.properties`.
Also GAV, artifactId and groupId are written to the same properties file.

## Usage

### Activation

The `target/nextversion.properties` file is always written.

When maven is invoked with `nu.aron:nextversion-maven-plugin:run` on the command line.
The plugin will query the binary repository to find out what the next version shall be.
Then the pom file will be modified.
Then the `target/nextversion.properties` will be written with the same properties.

Without `nu.aron:nextversion-maven-plugin:run` the version will be taken from the pom file.
The `target/nextversion.properties` will contain the same values except version.

Hence,
```sh
mvn nu.aron:nextversion-maven-plugin:run validate
```
will query the binary repo then write pom and `target/nextversion.properties`.

This will set the version and deploy:

```sh
mvn nu.aron:nextversion-maven-plugin:run deploy
```

If the `~/.m2/settings.xml`file contains:

```xml
  <pluginGroups>
    <pluginGroup>nu.aron</pluginGroup>
  </pluginGroups>
```

It is possible to use

```sh
mvn  nextversion:run deploy

```

### Adding to a project

Relies on the .mvn directory functionality added in maven-3.3.1.
In `.mvn/extensions.xml` add the following:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<extensions>
  <extension>
    <groupId>nu.aron</groupId>
    <artifactId>nextversion-maven-plugin</artifactId>
    <version>SEE_DOWNLOAD_LINK_ABOVE</version>
  </extension>
</extensions>
```

In order to stamp the jar with the git checksum configure the maven-jar-plugin:

```xml
<build>
  <plugins>
      <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-jar-plugin</artifactId>
      <version>3.2.0</version>
      <configuration>
        <archive>
          <manifestEntries>
            <commit>${nextversion.commit}</commit>
          </manifestEntries>
        </archive>
      </configuration>
      </plugin>
  </plugins>
</build>
```

## Roadmap

* Add autorelease for this project
* Verify functionality with Artifactory
* Verify functionality with GitLab

## Release to maven central

```
mvn -DperformRelease=true deploy
```
