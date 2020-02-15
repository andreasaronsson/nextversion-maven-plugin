package nu.aron.nextbuildnumber;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.codehaus.mojo.versions.ordering.MavenVersionComparator;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static nu.aron.nextbuildnumber.Constants.MASTER;
import static org.apache.commons.lang3.StringUtils.countMatches;

interface Incrementable {
    default String newVersion(String currentVersion, String branch) {
        ArtifactVersion version = new DefaultArtifactVersion(currentVersion);
        var versionComparator = new MavenVersionComparator();

        if (countMatches(currentVersion, '.') == 0) {
            var retval = versionComparator.incrementSegment(version, 0);
            return addBranch(valueOf(retval.getMajorVersion()), branch);
        }
        if (countMatches(currentVersion, '.') == 1) {
            var retval = versionComparator.incrementSegment(version, 1);
            return addBranch(format("%d.%d", retval.getMajorVersion(), retval.getMinorVersion()), branch);
        }
        return addBranch(versionComparator.incrementSegment(version, 2).toString(), branch);
    }

    private String addBranch(String version, String branch) {
        if (branch.equals(MASTER)) {
            return version;
        }
        return version + "-" + branch.replace("/", "-");
    }
}
