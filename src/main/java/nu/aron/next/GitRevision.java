package nu.aron.next;

import org.apache.maven.execution.MavenSession;

import static nu.aron.next.CommandInDirectory.run;
import static nu.aron.next.Constants.NEXT_COMMIT;
import static nu.aron.next.Constants.GIT_REVISION;
import static nu.aron.next.Constants.log;
import static nu.aron.next.CurrentWorkingDirectory.getCwd;

interface GitRevision {

    default void revision(MavenSession session) {
        logAndSetProperty(session, run(getCwd(session), GIT_REVISION));
    }

    private void logAndSetProperty(MavenSession session, String value) {
        session.getSystemProperties().setProperty(NEXT_COMMIT, value);
        session.getUserProperties().setProperty(NEXT_COMMIT, value);
        log("System property {} set to {}", NEXT_COMMIT, value);
    }
}
