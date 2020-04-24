package nu.aron.nextbuildnumber;

import io.vavr.control.Option;
import org.apache.commons.io.input.NullInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

import static org.fusesource.jansi.Ansi.ansi;

class Constants {
    static Logger logger = LoggerFactory.getLogger("nextversion-maven-plugin");
    static final String EMPTY = "";
    static final String COMMIT = "nextversion.commit";
    static final String VERSION = "nextversion.version";
    static final String MASTER = "master";
    static final String GIT_BRANCH = "git rev-parse --abbrev-ref HEAD";
    static final String GIT_REVISION = "git rev-parse HEAD";
    static final String SNAPSHOT = "SNAPSHOT";
    private static final String LOGNAME = "nextversion-maven-plugin:" + version() + ":run";

    private Constants() {
    }

    private static String version() {
        var stream = Option.of(Constants.class.getClassLoader().getResourceAsStream("META-INF/maven/nu.aron/nextversion-maven-plugin/pom.properties"));
        try {
            Properties p = new Properties();
            p.load(stream.getOrElse(new NullInputStream(0)));
            return String.valueOf(p.get("version"));
        } catch (IOException e) {
            throw new PluginException(e);
        }
    }

    static void log(String message, String... args) {
        logger.info("--- " + ansi().fgGreen().a(LOGNAME).reset() + " ---"); // NOSONAR
        logger.info(message, (Object[]) args);
    }
}
