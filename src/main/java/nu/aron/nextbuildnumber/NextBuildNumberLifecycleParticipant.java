package nu.aron.nextbuildnumber;

import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import kong.unirest.Unirest;
import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.ModelReader;
import org.apache.maven.model.io.ModelWriter;
import org.apache.maven.shared.utils.cli.CommandLineUtils;
import org.apache.maven.shared.utils.cli.CommandLineUtils.StringStreamConsumer;
import org.apache.maven.shared.utils.cli.Commandline;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static java.lang.String.join;
import static org.apache.commons.lang3.StringUtils.removeEnd;
import static org.apache.commons.lang3.StringUtils.substringsBetween;

/**
 * Queries the deployment repo for current latest version.
 * Sets the version to current latest version +1
 */
@Component(role = AbstractMavenLifecycleParticipant.class, hint = "NextBuildNumberLifecycleParticipant")
public class NextBuildNumberLifecycleParticipant extends AbstractMavenLifecycleParticipant {

    private static Logger log = LoggerFactory.getLogger(NextBuildNumberLifecycleParticipant.class);
    @Requirement
    private ModelWriter modelWriter;
    @Requirement
    private ModelReader modelReader;

    @Override
    public void afterSessionStart(MavenSession session) throws MavenExecutionException {
        var pom = session.getRequest().getPom().getAbsoluteFile();
        var model = Try.of(() -> modelReader.read(pom, null)).getOrElseThrow(e -> new MavenExecutionException(e.getMessage(), e));
        var xmlData = xmlData(session, model);
        var version = versionFromString(xmlData).getOrElseThrow(() -> new MavenExecutionException("No data found", new Throwable()));
        var nextVersion = findNextNumber(version);
        log.info("Version {}", nextVersion);
        model.setVersion(nextVersion);
        Try.run(() -> modelWriter.write(pom, null, model));
    }

    String xmlData(MavenSession session, Model model) {
        return List.ofAll(session.getRequest().getProjectBuildingRequest().getRemoteRepositories())
                .map(ArtifactRepository::getUrl)
                .map(u -> urlFromRepo(u, model))
                .map(u -> Unirest.get(u).asString().getBody())
                .reject(s -> s.contains("404 Not Found")).toCharSeq().toString();
    }

    String findNextNumber(String version) {
        ArtifactVersion av = new DefaultArtifactVersion(version);
        if (version.length() > 3) {
            return av.getMajorVersion() + "." + av.getMinorVersion() + "." + (av.getIncrementalVersion() + 1);
        } else if (version.length() > 1) {
            return av.getMajorVersion() + "." + (av.getMinorVersion() + 1);
        }
        return "" + (av.getMajorVersion() + 1);
    }


    Option<String> versionFromString(String data) {
        String[] found = substringsBetween(data, "<latest>", "</latest>");
        if (found.length == 0) {
            return Option.none();
        }
        return Option.of(found[0]);
    }

    String urlFromRepo(String repoUrl, Model model) {
        var groupId = Option.of(model.getGroupId()).getOrElse(() -> model.getParent().getGroupId());
        return join("/", removeEnd(repoUrl, "/"), groupId.replace('.', '/'), model.getArtifactId(), "maven-metadata.xml");
    }

    @Override
    public final void afterProjectsRead(MavenSession session) throws MavenExecutionException {
        if (isDryRun(session)) {
            log.info("Dry run. No actions will be taken.");
        } else {
            try {
                doWork(session);
            } catch (PluginException e) {
                throw new MavenExecutionException(e.getMessage(), e.getCause());
            }
        }

    }

    private boolean isDryRun(MavenSession s) {
        return Option.of(s.getUserProperties().get("dryRun")).isDefined();
    }

    private void doWork(MavenSession session) {
        setGitRevision(session);
    }

    void setGitRevision(MavenSession session) {
        logAndSetProperty(session, run(session.getCurrentProject().getBasedir()));
    }

    String run(File workingDirectory) {
        log.info("Running {}", "git rev-parse HEAD");
        Commandline cl = new Commandline("git rev-parse HEAD");
        cl.setWorkingDirectory(workingDirectory);
        StringStreamConsumer stdout = new StringStreamConsumer();
        Try.of(() -> CommandLineUtils.executeCommandLine(cl, stdout, new LoggingConsumer()))
                .getOrElseThrow(e -> new PluginException((Exception) e));
        return stdout.getOutput();
    }

    private static class LoggingConsumer extends StringStreamConsumer {
        @Override
        public void consumeLine(String line) {
            log.error("{}", line);
        }
    }

    private void logAndSetProperty(MavenSession session, String value) {
        session.getSystemProperties().setProperty("commit", value);
        session.getUserProperties().setProperty("commit", value);
        session.getCurrentProject().getProperties().setProperty("commit", value);
        log.info("{} set to {}", "commit", value);
    }
}
