package nu.aron.next;

import io.vavr.control.Try;
import org.apache.maven.execution.MavenSession;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static nu.aron.next.CurrentWorkingDirectory.getCwd;

interface GitRevision extends LogAndSet, Branch {

    default void revision(MavenSession session) {
        File cwd = getCwd(session);
        Path p = find(name(cwd), cwd);
        String rev = Try.of(() -> Files.readString(p)).
                getOrElseThrow(PluginException::new).trim();
        put(session, rev);
    }

    private Path find(String name, File cwd) {
        if ("HEAD".equals(name)) {
            return cwd.toPath().resolve(".git").resolve("HEAD");
        }
        return cwd.toPath().resolve(".git").resolve("refs").resolve("heads").resolve(name);
    }
}
