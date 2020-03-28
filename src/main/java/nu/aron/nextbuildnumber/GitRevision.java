package nu.aron.nextbuildnumber;

import org.apache.maven.execution.MavenSession;

import static nu.aron.nextbuildnumber.CommandInDirectory.run;
import static nu.aron.nextbuildnumber.Constants.COMMIT;
import static nu.aron.nextbuildnumber.Constants.GIT_REVISION;
import static nu.aron.nextbuildnumber.Constants.log;
import static nu.aron.nextbuildnumber.CurrentWorkingDirectory.getCwd;

interface GitRevision {

    default void setRevision(MavenSession session) {
        logAndSetProperty(session, run(getCwd(session), GIT_REVISION));
    }

    private void logAndSetProperty(MavenSession session, String value) {
        session.getSystemProperties().setProperty(COMMIT, value);
        session.getUserProperties().setProperty(COMMIT, value);
        log("System property {} set to {}", COMMIT, value);
    }
}
