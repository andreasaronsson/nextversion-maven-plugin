package nu.aron.nextbuildnumber;

import org.apache.maven.shared.utils.cli.CommandLineUtils.StringStreamConsumer;

import static nu.aron.nextbuildnumber.Constants.logger;

class LoggingConsumer extends StringStreamConsumer {
    @Override
    public void consumeLine(String line) {
        logger.error("{}", line);
    }
}
