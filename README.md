# Nextversion maven plugin

Increments versions CD style.
One binary is created per build.
Each binary contains the sha git checksum.
This assumes that only the viable binaries are saved.
This also assumes that tagging of what binaries are actually put in production are made after all tests are run.

Increments the patch part of the project version before the build begins.
This means that `1.0.0` is changed to `1.0.1` and `2.0.99` is changed to `2.0.100` and so on.
Queries the deployment repo to know 'previous version'.
The version present in the pom file is only used as fallback.

System property `${commit}` is the value of git commit.
This value is intended to be used in the jar MANIFEST.MF or the like.

Release with `mvn deploy`.

## Usage

Relies on the .mvn directory functionality added in maven-3.3.1.
In `.mvn/extensions.xml` add the following:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<extensions>
  <extension>
    <groupId>nu.aron</groupId>
    <artifactId>nextversion-maven-plugin</artifactId>
    <version>1</version>
  </extension>
</extensions>
```
