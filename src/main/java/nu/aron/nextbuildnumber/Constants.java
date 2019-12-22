package nu.aron.nextbuildnumber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

import static org.fusesource.jansi.Ansi.ansi;

class Constants {
    static Logger log = LoggerFactory.getLogger("nextversion-maven-plugin");
    static final String COMMIT = "commit";
    static final String LOGNAME = "nextversion-maven-plugin:" + version() + ":next";

    static String version() {
        var stream = Constants.class.getClassLoader().getResourceAsStream("META-INF/maven/nu.aron/nextversion-maven-plugin/pom.properties");
        try {
            Properties p = new Properties();
            p.load(stream);
            return String.valueOf(p.get("version"));
        } catch (IOException e) {
            throw new PluginException(e);
        }
    }

    static void log(String message, String... args) {
        log.info("--- " + ansi().fgGreen().a(LOGNAME).reset().toString() + " ---");
        log.info(message, args);
    }
}
