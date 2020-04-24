package nu.aron.nextbuildnumber;

import io.vavr.collection.List;
import org.apache.maven.execution.MavenSession;

import java.util.Properties;

import static io.vavr.control.Option.of;
import static nu.aron.nextbuildnumber.Constants.log;

interface Activator {

    default boolean activated(MavenSession session) {
        if (skipped(session.getUserProperties())) {
            return false;
        }
        if (hasNextGoal(session)) {
            log("Activated by nextversion goal.");
            return true;
        }
        return false;
    }

    private boolean hasNextGoal(MavenSession session) {
        var goals = List.ofAll(session.getRequest().getGoals()).mkString();
        return goals.contains("nextversion") || goals.contains("nextversion-maven-plugin");
    }

    private boolean skipped(Properties userProperties) {
        return of(userProperties.get("next.skip")).isDefined();
    }
}
