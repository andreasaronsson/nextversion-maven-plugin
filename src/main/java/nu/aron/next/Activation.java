package nu.aron.next;

import org.apache.maven.execution.MavenSession;

@FunctionalInterface
interface Activation {
    boolean test(MavenSession session);
}