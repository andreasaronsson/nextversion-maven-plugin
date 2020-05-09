package nu.aron.next;

import org.apache.maven.execution.MavenSession;

import java.io.File;

interface CurrentWorkingDirectory {
    static File getCwd(MavenSession session) {
        return new File(session.getRequest().getBaseDirectory());
    }
}
