package nu.aron.nextbuildnumber;

import com.google.gson.internal.bind.util.ISO8601Utils;
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
import org.fusesource.jansi.AnsiConsole;

import java.io.File;
import java.nio.file.Paths;
import java.util.function.Consumer;

import static java.util.function.Function.identity;
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
        AnsiConsole.systemInstall();
        if (!isDeployGoal(session) || skipped(session)) {
            log("Not deply goal or skipped. Nothing to do.");
        } else {
            log("Deploy goal. Version will be incremented.");
            doWork(this::witeNewVersion, session);
        }
    }

    @Override
    public final void afterProjectsRead(MavenSession session) throws MavenExecutionException {
        if (skipped(session)) {
            log("Skipped. Will not set commit property to git checksum.");
        } else {
            doWork(this::setRevision, session);
        }
        AnsiConsole.systemUninstall();
    }

    private boolean skipped(MavenSession s) {
        return Option.of(s.getUserProperties().get("next.skip")).isDefined();
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
        findModels(List.of(model)).forEach(m -> persistVersion(nextVersion, m));
    }

    Model modelFromFile(File file) {
        if (file.isDirectory()) {
            return modelFromPom(file.toPath().resolve("pom.xml").toFile());
        }
        return modelFromPom(file);
    }

    Model modelFromPom(File pom) {
        return Try.of(() -> modelReader.read(pom, null)).getOrElseThrow(PluginException::new);
    }

    List<Model> findModels(List<Model> models) {
        if (models.isEmpty()) {
            return models;
        }
        return models.appendAll(findModels(models.map(this::fromModel).flatMap(identity()).map(this::modelFromFile)));
    }

    List<File> fromModel(Model m) {
        return List.ofAll(m.getModules()).map(module -> Paths.get(m.getProjectDirectory().toString(), module).toFile());
    }

    void persistVersion(String nextVersion, Model model) {
        model.setVersion(nextVersion);
        Try.run(() -> modelWriter.write(model.getPomFile(), null, model));
    }
}
