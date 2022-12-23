package nu.aron.next;

import io.vavr.control.Try;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.maven.execution.MavenSession;

import java.io.File;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;
import static nu.aron.next.CurrentWorkingDirectory.getCwd;

interface GitRevision extends LogAndSet, Branch {

    default void revision(MavenSession session) {
        Path p = findPath(getCwd(session));
        String rev = Try.of(() -> new ReversedLinesFileReader(p.toFile(), UTF_8).readLine()).
                getOrElseThrow(PluginException::new).trim();
        put(session, rev.split(" ")[1].trim());
    }

    private Path findPath(File cwd) {
        return cwd.toPath().resolve(".git").resolve("logs").resolve("HEAD");
    }
}
