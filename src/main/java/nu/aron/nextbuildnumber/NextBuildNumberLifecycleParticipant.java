package nu.aron.nextbuildnumber;

import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.ModelReader;
import org.apache.maven.model.io.ModelWriter;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import java.io.File;
import java.nio.file.Paths;
import java.util.function.Consumer;

import static nu.aron.nextbuildnumber.Constants.log;

/**
 * Queries the deployment repo for current latest version.
 * Sets the version to current latest version +1
 */
@Component(role = AbstractMavenLifecycleParticipant.class, hint = "NextBuildNumberLifecycleParticipant")
public class NextBuildNumberLifecycleParticipant extends AbstractMavenLifecycleParticipant implements Incrementable, GitRevision, RemoteVersion {

    @Requirement
    private ModelWriter modelWriter;
    @Requirement
    private ModelReader modelReader;

    @Override
    public void afterSessionStart(MavenSession session) throws MavenExecutionException {
        if (!isDeployGoal(session) || isDryRun(session)) {
            log("Not deply goal or dryRun. Nothing to do.");
        } else {
            log("Deploy goal. Version will be incremented.");
            doWork(this::witeNewVersion, session);
        }
    }

    @Override
    public final void afterProjectsRead(MavenSession session) throws MavenExecutionException {
        if (isDryRun(session)) {
            log("Dry run. Will not set commit property to git checksum.");
        } else {
            doWork(this::setRevision, session);
        }
    }

    private boolean isDryRun(MavenSession s) {
        return Option.of(s.getUserProperties().get("dryRun")).isDefined();
    }

    private boolean isDeployGoal(MavenSession s) {
        return s.getRequest().getGoals().contains("deploy");
    }

    private void doWork(Consumer<MavenSession> c, MavenSession s) throws MavenExecutionException {
        try {
            c.accept(s);
        } catch (PluginException e) {
            throw new MavenExecutionException(e.getMessage(), e);
        }
    }

    private void witeNewVersion(MavenSession session) {
        var pom = session.getRequest().getPom().getAbsoluteFile();
        var model = modelFromFile(pom);
        var version = getCurrent(session, model);
        log("Latest released version {}", version);
        var nextVersion = newVersion(version);
        log("Next version {}", nextVersion);

        var f = findModules(List.ofAll(model.getModules()));
    }

    private Model modelFromFile(File pom) {
        return Try.of(() -> modelReader.read(pom, null)).getOrElseThrow(PluginException::new);
    }

    private List<Model> findModules(List<String> moduleNames) {
        if (moduleNames.isEmpty()) {
            return List.empty();
        }
        return moduleNames.map(n -> Paths.get(n).resolve("pom.xml"))
                .reject(f -> !f.toFile().exists())
                .map(f -> modelFromFile(f.toFile()));
    }

    private void persistVersion(String nextVersion, Model model, File pom) {
        model.setVersion(nextVersion);
        Try.run(() -> modelWriter.write(pom, null, model));
    }
}
