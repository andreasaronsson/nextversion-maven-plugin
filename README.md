# Nextversion maven plugin

Increments versions CD style.
One binary is created per build.
Each binary contains the sha git checksum.
This assumes that only the viable binaries are saved.
Or else retention becomes a problem faster.

Queries the deployment repo to know 'previous version'.
Then increments the version number.
When the version number has three segments (https://semver.org).
Increments the patch part of the project version before the build begins.
This means that `1.0.0` is changed to `1.0.1` and `2.0.99` is changed to `2.0.100` and so on.
The version present in the pom file is only used as fallback.
When there is only one segment this number is incremented.
This means that `1` is changed to `2` and so on.

Any other version propagation must be done in a different way.
One way is to do manual deploy of the first major or minor version.

System property `${commit}` is the value of git commit.
This value is intended to be used in the jar MANIFEST.MF or the like.

Release with `mvn deploy`.
The plugin is only activated if maven was invoked with the deploy goals.
For local builds with e.g. `mvn verify` no action will be taken.

## Usage

Relies on the .mvn directory functionality added in maven-3.3.1.
In `.mvn/extensions.xml` add the following:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<extensions>
  <extension>
    <groupId>nu.aron</groupId>
    <artifactId>nextversion-maven-plugin</artifactId>
    <version>FIND_LATEST_VERSION</version>
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
            <commit>${commit}</commit>
          </manifestEntries>
        </archive>
      </configuration>
      </plugin>
  </plugins>
</build>
```