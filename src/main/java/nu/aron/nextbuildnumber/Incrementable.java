package nu.aron.nextbuildnumber;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.codehaus.mojo.versions.ordering.MavenVersionComparator;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.countMatches;

interface Incrementable {
    default String newVersion(String currentVersion) {
        ArtifactVersion version = new DefaultArtifactVersion(currentVersion);
        var versionComparator = new MavenVersionComparator();

        if (countMatches(currentVersion, '.') == 0) {
            var retval = versionComparator.incrementSegment(version, 0);
            return valueOf(retval.getMajorVersion());
        }
        if (countMatches(currentVersion, '.') == 1) {
            var retval = versionComparator.incrementSegment(version, 1);
            return format("%d.%d", retval.getMajorVersion(), retval.getMinorVersion());
        }
        return versionComparator.incrementSegment(version, 2).toString();
    }
}
