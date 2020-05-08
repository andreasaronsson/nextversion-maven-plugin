package nu.aron.nextbuildnumber;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Objects.isNull;
import static nu.aron.nextbuildnumber.Constants.MASTER;
import static nu.aron.nextbuildnumber.Constants.SNAPSHOT;
import static nu.aron.nextbuildnumber.Constants.log;
import static org.apache.commons.lang3.StringUtils.isEmpty;

interface Incrementable {
    default String newVersion(String currentVersion, String branch, int increment) {
        ArtifactVersion version = new DefaultArtifactVersion(currentVersion);

        if (countDots(currentVersion) == 0) {
            return addBranch(valueOf(version.getMajorVersion() + increment), branch);
        }
        if (countDots(currentVersion) == 1) {
            return addBranch(format("%d.%d", version.getMajorVersion(), version.getMinorVersion() + increment), branch);
        }
        return addBranch(format("%d.%d.%d", version.getMajorVersion(), version.getMinorVersion(), version.getIncrementalVersion() + increment), branch);
    }

    private String addBranch(String version, String branch) {
        if (branch.equals(MASTER) || isEmpty(branch)) {
            return version;
        }
        return version + "-" + branch.replace("/", "-");
    }

    default int countDots(CharSequence str) {
        return (int)str.chars().filter(c -> c == '.').count();
    }

    default String manuallyBumped(String pomVersion, String remoteVersion) {
        var pom = new DefaultArtifactVersion(pomVersion);
        var remote = new DefaultArtifactVersion(remoteVersion);
        if (isManual(pom, remote)) {
            log("Manual version bump identified.");
            return newVersion(pomVersion, "", 0);
        }
        return remoteVersion;
    }

    private boolean isManual(ArtifactVersion pom, ArtifactVersion remote) {
        if (1 == pom.compareTo(remote)) {
            // We assume this version is higher
            return isNull(pom.getQualifier()) || SNAPSHOT.equals(pom.getQualifier());
        }
        return false;
    }
}
