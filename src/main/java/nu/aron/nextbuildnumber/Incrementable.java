package nu.aron.nextbuildnumber;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static nu.aron.nextbuildnumber.Constants.MASTER;
import static org.apache.commons.lang3.StringUtils.countMatches;

interface Incrementable {
    default String newVersion(String currentVersion, String branch) {
        ArtifactVersion version = new DefaultArtifactVersion(currentVersion);

        if (countMatches(currentVersion, '.') == 0) {
            return addBranch(valueOf(version.getMajorVersion() + 1), branch);
        }
        if (countMatches(currentVersion, '.') == 1) {
            return addBranch(format("%d.%d", version.getMajorVersion(), version.getMinorVersion() + 1), branch);
        }
        return addBranch(format("%d.%d.%d", version.getMajorVersion(), version.getMinorVersion(), version.getIncrementalVersion() + 1), branch);
    }

    private String addBranch(String version, String branch) {
        if (branch.equals(MASTER)) {
            return version;
        }
        return version + "-" + branch.replace("/", "-");
    }
}
