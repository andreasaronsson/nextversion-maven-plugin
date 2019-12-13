package nu.aron.nextbuildnumber;

import org.apache.maven.execution.MavenSession;

import static nu.aron.nextbuildnumber.CommandInDirectory.run;
import static nu.aron.nextbuildnumber.Constants.COMMIT;
import static nu.aron.nextbuildnumber.Constants.log;

interface GitRevision {

    default void setRevision(MavenSession session) {
        logAndSetProperty(session, run(session.getCurrentProject().getBasedir()));
    }

    private void logAndSetProperty(MavenSession session, String value) {
        session.getSystemProperties().setProperty(COMMIT, value);
        session.getUserProperties().setProperty(COMMIT, value);
        session.getCurrentProject().getProperties().setProperty(COMMIT, value);
        log("project property {}={}", COMMIT, value);
    }
}
