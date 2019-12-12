package nu.aron.nextbuildnumber;

import io.vavr.CheckedFunction3;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

class Constants {
    static Logger log = LoggerFactory.getLogger("nextversion-maven-plugin");
    static final String COMMIT = "commit";
    static final String LOGNAME = "--- nextversion-maven-plugin:" + version();

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
}
