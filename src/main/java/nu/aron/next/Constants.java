package nu.aron.next;

import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.commons.io.input.NullInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static java.lang.String.join;
import static org.fusesource.jansi.Ansi.ansi;

class Constants {
    static final String EMPTY = "";
    static final String VERSION = "version";
    static final String RUN = "run";
    static final String NEXT_COMMIT = "nextversion.commit";
    static final String NEXT_VERSION = "nextversion.version";
    static final String MASTER = "master";
    static final String MAIN = "main";
    static final String SNAPSHOT = "SNAPSHOT";
    static final String ARTIFACT_ID = "artifactId";
    static final String GROUP_ID = "groupId";
    static final String NEXTVERSION_MAVEN_PLUGIN = "nextversion-maven-plugin";
    private static final String LOGNAME = join(":", NEXTVERSION_MAVEN_PLUGIN, version(), RUN);
    static Logger logger = LoggerFactory.getLogger(NEXTVERSION_MAVEN_PLUGIN);

    private Constants() {
        // Hide the public constructor
    }

    private static String version() {
        var stream = Option.of(Constants.class.getClassLoader().getResourceAsStream("META-INF/maven/nu.aron/nextversion-maven-plugin/pom.properties"));
        return Try.of(() -> {
            Properties p = new Properties();
            p.load(stream.getOrElse(new NullInputStream(0)));
            return String.valueOf(p.get(VERSION));
        }).getOrElseThrow(PluginException::new);
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
