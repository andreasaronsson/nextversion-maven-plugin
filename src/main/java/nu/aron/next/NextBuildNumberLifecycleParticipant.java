package nu.aron.next;

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

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.function.Consumer;

import static java.lang.String.join;
import static java.util.Objects.isNull;
import static nu.aron.next.Constants.ARTIFACT_ID;
import static nu.aron.next.Constants.GROUP_ID;
import static nu.aron.next.Constants.NEXT_COMMIT;
import static nu.aron.next.Constants.NEXT_VERSION;
import static nu.aron.next.Constants.VERSION;
import static nu.aron.next.Constants.log;
import static nu.aron.next.Constants.logError;
import static nu.aron.next.CurrentWorkingDirectory.getCwd;
import static org.apache.maven.shared.utils.StringUtils.isEmpty;

/**
 * Queries the deployment repo for current latest version.
 * Sets the version to current latest version +1
 */
@Component(role = AbstractMavenLifecycleParticipant.class, hint = "NextBuildNumberLifecycleParticipant")
public class NextBuildNumberLifecycleParticipant extends AbstractMavenLifecycleParticipant implements Incrementable,
        GitRevision, RemoteVersion, Activator, Branch, Modelbuilder {

    private final Activation active = this::activated;
    @Requirement
    private ModelWriter modelWriter;
    @Requirement
    private ModelReader modelReader;
    @Override
    public void afterSessionStart(MavenSession session) throws MavenExecutionException {
        AnsiConsole.systemInstall();
        doWork(this::revision, session);
        doWork(this::version, session);
        doWork(this::write, session);
        AnsiConsole.systemUninstall();
    }

    private void doWork(Consumer<MavenSession> c, MavenSession s) throws MavenExecutionException {
        try {
            c.accept(s);
        } catch (PluginException e) {
            throw new MavenExecutionException(e.getMessage(), e);
        }
    }

    private void version(MavenSession session) {
        var pom = session.getRequest().getPom().getAbsoluteFile();
        var model = modelFromFile(pom, modelReader).getOrElse(new Model());
        if (isEmptyModel(model)) {
            log("Unable to create maven model. Skipping.");
            session.getUserProperties().put("next.skip", "true");
        } else {
            checkVersionPresent(model);
        }
        if (active.test(session)) {
            String remoteVersion = getRemote(session, model);
            File cwd = getCwd(session);
            if (!isEmpty(remoteVersion)) {
                log("Latest released version {}", remoteVersion);
            }
            var version = manuallyBumped(model.getVersion(), remoteVersion);
            String nextVersion;
            if (version._2) {
                nextVersion = version._1;
            } else {
                nextVersion = newVersion(version._1, name(cwd), defaultName(cwd), 1);
            }
            session.getSystemProperties().setProperty(NEXT_VERSION, nextVersion);
            log("Next version {}", nextVersion);
        }
    }

    private boolean isEmptyModel(Model model) {
        return "[inherited]:null:jar:[inherited]".equals(model.toString());
    }

    private void write(MavenSession session) {
        if (active.test(session)) {
            var pom = session.getRequest().getPom().getAbsoluteFile();
            var model = modelFromFile(pom, modelReader).get();
            String nextVersion = session.getSystemProperties().getProperty(NEXT_VERSION, model.getVersion());
            saveValues(nextVersion, session, model);
            findModels(List.of(model), modelReader).forEach(m -> persistVersion(nextVersion, m));
        }
    }

    private void checkVersionPresent(Model model) {
        if (isNull(model.getVersion())) {
            logError("No version present in pom.");
            throw new PluginException(new Throwable("No version"));
        }
    }

    private void persistVersion(String nextVersion, Model model) {
        // This version can safely be set in all modules in a multi module build as it is never committed to VCS.
        model.setVersion(nextVersion);
        Try.run(() -> modelWriter.write(model.getPomFile(), null, model))
                .onFailure(e -> logError("Failed to write ", e.getMessage()));
    }

    private void saveValues(String nextVersion, MavenSession session, Model model) {
        var p = new Properties();
        p.put("commit", session.getSystemProperties().get(NEXT_COMMIT));
        p.put(VERSION, nextVersion);
        p.put(ARTIFACT_ID, model.getArtifactId());
        p.put(GROUP_ID, groupIdFromModel(model));
        p.put("gav", join(":", groupIdFromModel(model), model.getArtifactId(), nextVersion));
        var target = Paths.get("target").toFile();
        if (!target.mkdir() && !target.exists()) {
            logError("Unable to create target directory!");
            throw new PluginException(new IllegalStateException("Unable to create target directory"));
        }
        Try.run(() -> p.store(new FileOutputStream("target/nextversion.properties"), null))
                .onFailure(e -> logError("Failed to write ", e.getMessage()));
    }
}
