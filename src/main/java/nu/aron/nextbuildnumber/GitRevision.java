package nu.aron.nextbuildnumber;

import org.apache.maven.execution.MavenSession;
import org.slf4j.Marker;

import static nu.aron.nextbuildnumber.CommandInDirectory.run;
import static nu.aron.nextbuildnumber.Constants.*;

interface GitRevision {

    default void set(MavenSession session) {
        logAndSetProperty(session, run(session.getCurrentProject().getBasedir()));
    }

    private void logAndSetProperty(MavenSession session, String value) {
        session.getSystemProperties().setProperty(COMMIT, value);
        session.getUserProperties().setProperty(COMMIT, value);
        session.getCurrentProject().getProperties().setProperty(COMMIT, value);
        log.info("{} project property {}={}", LOGNAME, COMMIT, value);
    }
}
