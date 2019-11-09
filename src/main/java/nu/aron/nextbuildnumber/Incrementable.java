package nu.aron.nextbuildnumber;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.codehaus.mojo.versions.ordering.MavenVersionComparator;

interface Incrementable {
    default String newVersion(String currentVersion) {
        var versionComparator = new MavenVersionComparator();
        ArtifactVersion version = new DefaultArtifactVersion(currentVersion);
        return versionComparator.incrementSegment(version, 2).toString();
    }
}
