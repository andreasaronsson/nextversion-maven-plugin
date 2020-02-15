package nu.aron.nextbuildnumber;

import io.vavr.control.Try;
import org.apache.maven.shared.utils.cli.CommandLineUtils;
import org.apache.maven.shared.utils.cli.CommandLineUtils.StringStreamConsumer;
import org.apache.maven.shared.utils.cli.Commandline;

import java.io.File;

interface CommandInDirectory {
    static String run(File workingDirectory, String command) {
        Commandline cl = new Commandline(command);
        cl.setWorkingDirectory(workingDirectory);
        StringStreamConsumer stdout = new StringStreamConsumer();
        Try.of(() -> CommandLineUtils.executeCommandLine(cl, stdout, new LoggingConsumer()))
                .getOrElseThrow(PluginException::new);
        return stdout.getOutput().strip();
    }
}
