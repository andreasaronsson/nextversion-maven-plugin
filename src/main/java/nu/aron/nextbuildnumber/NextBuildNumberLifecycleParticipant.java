package nu.aron.nextbuildnumber;

import io.vavr.collection.List;
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

import java.io.FileOutputStream;
import java.util.Properties;
import java.util.function.Consumer;

import static nu.aron.nextbuildnumber.Constants.COMMIT;
import static nu.aron.nextbuildnumber.Constants.VERSION;
import static nu.aron.nextbuildnumber.Constants.log;
import static nu.aron.nextbuildnumber.CurrentWorkingDirectory.getCwd;

/**
 * Queries the deployment repo for current latest version.
 * Sets the version to current latest version +1
 */
@Component(role = AbstractMavenLifecycleParticipant.class, hint = "NextBuildNumberLifecycleParticipant")
public class NextBuildNumberLifecycleParticipant extends AbstractMavenLifecycleParticipant implements Incrementable,
        GitRevision, RemoteVersion, Activator, BranchName, Modelbuilder {

    @Requirement
    private ModelWriter modelWriter;
    @Requirement
    private ModelReader modelReader;
    private final Activation active = this::activated;

    @Override
    public void afterSessionStart(MavenSession session) throws MavenExecutionException {
        AnsiConsole.systemInstall();
        if (active.test(session)) {
            log("Version will be incremented and commit property will be set.");
            doWork(this::setRevision, session);
            doWork(this::witeNewVersion, session);
        }
        AnsiConsole.systemUninstall();
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
        var model = modelFromFile(pom, modelReader);
        checkModel(model);
        model.getProperties().put(COMMIT, session.getSystemProperties().get(COMMIT));
        var version = manuallyBumped(model.getVersion(), getCurrent(session, model));
        log("Latest released version {}", version);
        var nextVersion = newVersion(version, branchName(getCwd(session)), 1);
        session.getSystemProperties().setProperty(VERSION, nextVersion);
        log("Next version {}", nextVersion);
        saveValues(nextVersion, session);
        findModels(List.of(model), modelReader).forEach(m -> persistVersion(nextVersion, m));
    }

    private void checkModel(Model model) {
        if (model.getVersion() == null) {
            log("No version present in pom.");
            throw new PluginException(new Throwable("No version"));
        }
    }

    private void persistVersion(String nextVersion, Model model) {
        // This version can safely be set in all modules in a multi module build as it is never committed to VCS.
        model.setVersion(nextVersion);
        Try.run(() -> modelWriter.write(model.getPomFile(), null, model));
    }

    private void saveValues(String nextVersion, MavenSession session) {
        var p = new Properties();
        p.put("commit", session.getSystemProperties().get("nextversion.commit"));
        p.put("version", nextVersion);
        Try.run(() -> p.store(new FileOutputStream("target/nextversion.properties"), null))
                .onFailure(e -> log("Failed to write ", e.getMessage()));
    }
}
