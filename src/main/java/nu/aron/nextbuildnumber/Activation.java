package nu.aron.nextbuildnumber;

import org.apache.maven.execution.MavenSession;

@FunctionalInterface
interface Activation {
    boolean test(MavenSession session, GetEnvPretender getEnvPretender);
}