package nu.aron.nextbuildnumber;

import org.apache.maven.execution.MavenSession;

import java.util.Properties;

import static io.vavr.control.Option.of;
import static nu.aron.nextbuildnumber.Constants.log;

interface Activator {

    default boolean activated(MavenSession session, GetEnvPretender getEnvPretender) {
        if (skipped(session.getUserProperties())) {
            return false;
        }
        if (!isCiBuild(getEnvPretender) && hasDeployGoal(session)) {
            log("Activated by deploy goal. No CI detected.");
            return true;
        }
        if (isCiBuild(getEnvPretender)) {
            log("Detected CI build.");
            if (hasDeployDeployGoal(session)) {
                log("Using deploy:deploy assuming version already set. Skipping.");
                return false;
            }
            return true;
        }
        return false;
    }

    private boolean isCiBuild(GetEnvPretender getEnvPretender) {
        return of(getEnvPretender.getenv("BRANCH_NAME")).isDefined() || of(getEnvPretender.getenv("CI")).isDefined();
    }

    private boolean hasDeployGoal(MavenSession session) {
        return session.getRequest().getGoals().contains("deploy");
    }

    private boolean hasDeployDeployGoal(MavenSession session) {
        return session.getRequest().getGoals().contains("deploy:deploy");
    }

    private boolean skipped(Properties userProperties) {
        return of(userProperties.get("next.skip")).isDefined();
    }
}
