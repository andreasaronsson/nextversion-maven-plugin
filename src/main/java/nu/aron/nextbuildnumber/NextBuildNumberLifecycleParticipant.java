package nu.aron.nextbuildnumber;

import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import kong.unirest.Unirest;
import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.ModelReader;
import org.apache.maven.model.io.ModelWriter;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import java.util.function.Consumer;

import static java.lang.String.join;
import static nu.aron.nextbuildnumber.Constants.log;
import static org.apache.commons.lang3.StringUtils.removeEnd;
import static org.apache.commons.lang3.StringUtils.substringBetween;

/**
 * Queries the deployment repo for current latest version.
 * Sets the version to current latest version +1
 */
@Component(role = AbstractMavenLifecycleParticipant.class, hint = "NextBuildNumberLifecycleParticipant")
public class NextBuildNumberLifecycleParticipant extends AbstractMavenLifecycleParticipant implements Incrementable, GitRevision {

    @Requirement
    private ModelWriter modelWriter;
    @Requirement
    private ModelReader modelReader;

    @Override
    public void afterSessionStart(MavenSession session) throws MavenExecutionException {
        if (!isDeployGoal(session) || isDryRun(session)) {
            // Do nothing
        } else {
            log("Deploy goal. Version will be incremented.");
            doWork(this::witeNewVersion, session);
        }
    }

    private void witeNewVersion(MavenSession session) {
        var pom = session.getRequest().getPom().getAbsoluteFile();
        var model = Try.of(() -> modelReader.read(pom, null)).getOrElseThrow(PluginException::new);
        var xmlData = xmlData(session, model);
        var version = versionFromString(xmlData)
                .onEmpty(() -> log("No previous release found."))
                .getOrElse(removeEnd(model.getVersion(), "-SNAPSHOT"));
        log("Latest released version {}", version);
        var nextVersion = newVersion(version);
        log("Next version {}", nextVersion);
        model.setVersion(nextVersion);
        Try.run(() -> modelWriter.write(pom, null, model));
    }

    private String xmlData(MavenSession session, Model model) {
        return List.ofAll(session.getRequest().getProjectBuildingRequest().getRemoteRepositories())
                .map(ArtifactRepository::getUrl)
                .map(u -> urlFromRepo(u, model))
                .map(u -> Unirest.get(u).asString().getBody())
                .reject(s -> s.contains("404 Not Found")).toCharSeq().toString();
    }

    private Option<String> versionFromString(String data) {
        return Option.of(substringBetween(data, "<release>", "</release>"));
    }

    private String urlFromRepo(String repoUrl, Model model) {
        var groupId = Option.of(model.getGroupId()).getOrElse(() -> model.getParent().getGroupId());
        return join("/", removeEnd(repoUrl, "/"), groupId.replace('.', '/'), model.getArtifactId(), "maven-metadata.xml");
    }

    @Override
    public final void afterProjectsRead(MavenSession session) throws MavenExecutionException {
        if (isDryRun(session)) {
            log("Dry run. Will not set commit property to git checksum.");
        } else {
            doWork(this::set, session);
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
}
