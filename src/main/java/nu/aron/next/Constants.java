package nu.aron.next;

import io.vavr.control.Option;
import org.apache.commons.io.input.NullInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

import static java.lang.String.join;
import static org.fusesource.jansi.Ansi.ansi;

class Constants {
    static final String EMPTY = "";
    static final String VERSION = "version";
    static final String COMMIT = "commit";
    static final String RUN = "run";
    static final String NEXT_COMMIT = "nextversion.commit";
    static final String NEXT_VERSION = "nextversion.version";
    static final String MASTER = "master";
    static final String GIT_BRANCH = "git rev-parse --abbrev-ref HEAD";
    static final String GIT_REVISION = "git rev-parse HEAD";
    static final String SNAPSHOT = "SNAPSHOT";
    static final String ARTIFACT_ID = "artifactId";
    static final String GROUP_ID = "groupId";
    static final String NEXTVERSION_MAVEN_PLUGIN = "nextversion-maven-plugin";
    static Logger logger = LoggerFactory.getLogger(NEXTVERSION_MAVEN_PLUGIN);
    private static final String LOGNAME = join(":", NEXTVERSION_MAVEN_PLUGIN, version(), RUN);

    private Constants() {
        // Hide the public constructor
    }

    private static String version() {
        var stream = Option.of(Constants.class.getClassLoader().getResourceAsStream("META-INF/maven/nu.aron/nextversion-maven-plugin/pom.properties"));
        try {
            Properties p = new Properties();
            p.load(stream.getOrElse(new NullInputStream(0)));
            return String.valueOf(p.get(VERSION));
        } catch (IOException e) {
            throw new PluginException(e);
        }
    }

    static void log(String message, String... args) {
        logger.info("--- " + ansi().fgGreen().a(LOGNAME).reset() + " ---"); // NOSONAR
        logger.info(message, args);
    }

    static void logError(String message, String... args) {
        logger.error("--- " + ansi().fgGreen().a(LOGNAME).reset() + " ---"); // NOSONAR
        logger.error(message, args);
    }
}
