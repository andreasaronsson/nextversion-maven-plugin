package nu.aron.next;

import org.apache.maven.shared.utils.cli.CommandLineUtils.StringStreamConsumer;

import static nu.aron.next.Constants.logger;

class LoggingConsumer extends StringStreamConsumer {
    @Override
    public void consumeLine(String line) {
        logger.error("{}", line);
    }
}
