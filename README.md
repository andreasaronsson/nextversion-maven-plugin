# Nextversion maven plugin

Usage:

Use project.version value `${revision}`.

Property `${commit}` is the value of git commit.

Release with `mvn validate;mvn deploy`.

The first invocation will write next version to `${project.basedir}/.mvn/jvm.config`

This plugin queries nexus for the latest released version.

These values can be sourced in a subsequent build.
Relies on the .mvn directory functionality added in maven-3.3.1.
Relies on the existence of file `target/changeset` containing a sha checksum.
The file is used to set the changeset property.
