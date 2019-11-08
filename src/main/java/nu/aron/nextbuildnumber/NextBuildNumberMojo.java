package nu.aron.nextbuildnumber;

import static java.lang.String.join;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import kong.unirest.Unirest;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.control.Option;
import io.vavr.control.Try;

@Mojo(name = "prepare", defaultPhase = LifecyclePhase.VALIDATE)
public class NextBuildNumberMojo extends AbstractMojo {

    private static Logger log = LoggerFactory.getLogger(NextBuildNumberMojo.class);
    private static final String NEXUS_URL = "https://nexus.host.name:8000/nexus/service/local/artifact/maven/resolve?r=release";
    private static final String DEFAULT_VERSION = "0.0.0";
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException {
        String url = getNexusURL(project);
        String currentVersion = queryNexusForVersion(url).getOrElse(DEFAULT_VERSION);
        String nextBuildNumber = findNextNumber(currentVersion);
        log.info("nextbuildnumberplugin next version number is {}", nextBuildNumber);
        Option<IOException> writeError = writeChanges(project, "-Drevision=" + nextBuildNumber);
        if (writeError.isDefined()) {
            throw new MojoExecutionException("Failed to write properties.", writeError.get());
        }
    }

    Option<String> queryNexusForVersion(String url) {
        return Try.of(() -> Unirest.get(url).header("accept", "application/json").asJson().getBody().getObject()
                .getJSONObject("data").get("version").toString()).onFailure(e -> {
                    log.error("Unable to query {}", url);
                    log.debug("", e);
                }).toOption();
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

    String getNexusURL(MavenProject p) {
        return join(EMPTY, NEXUS_URL, "&g=", p.getGroupId(), "&a=", p.getArtifactId(), "&p=", p.getPackaging(),
                "&v=RELEASE");
    }

    private Option<IOException> writeChanges(MavenProject project, String properties) {
        Path p = project.getBasedir().toPath().resolve(".mvn").resolve("jvm.config");
        try {
            p.getParent().toFile().mkdirs();
            Files.write(p, properties.getBytes());
        } catch (IOException e) {
            log.error("Unable to write to properties file.", e);
            return Option.of(e);
        }
        return Option.none();
    }
}
