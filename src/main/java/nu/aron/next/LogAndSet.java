package nu.aron.next;

import org.apache.maven.execution.MavenSession;

import static nu.aron.next.Constants.NEXT_COMMIT;
import static nu.aron.next.Constants.log;

public interface LogAndSet {
    default void put(MavenSession session, String value) {
        session.getSystemProperties().setProperty(NEXT_COMMIT, value);
        session.getUserProperties().setProperty(NEXT_COMMIT, value);
        log("System property {} set to '{}'", NEXT_COMMIT, value);
    }
}
