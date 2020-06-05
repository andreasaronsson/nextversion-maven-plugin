package nu.aron.next;

import io.vavr.collection.List;
import org.apache.maven.execution.MavenSession;

import java.util.Properties;

import static io.vavr.control.Option.of;
import static java.lang.String.join;
import static nu.aron.next.Constants.NEXTVERSION_MAVEN_PLUGIN;
import static nu.aron.next.Constants.RUN;
import static nu.aron.next.Constants.log;

interface Activator {

    default boolean activated(MavenSession session) {
        if (skipped(session.getUserProperties())) {
            log("Skip.");
            return false;
        }
        if (hasNextGoal(session)) {
            log("Activated by nextversion goal.");
            return true;
        }
        return false;
    }

    private boolean hasNextGoal(MavenSession session) {
        var commandlineGoals = List.ofAll(session.getRequest().getGoals());
        var activationGoals = List.of("nextversion", NEXTVERSION_MAVEN_PLUGIN, join(":", "nu.aron", NEXTVERSION_MAVEN_PLUGIN, RUN), join(":", NEXTVERSION_MAVEN_PLUGIN, RUN),"nu.aron:next:run");
        return !activationGoals.retainAll(commandlineGoals).isEmpty();
    }

    private boolean skipped(Properties userProperties) {
        return of(userProperties.get("next.skip")).isDefined();
    }
}
