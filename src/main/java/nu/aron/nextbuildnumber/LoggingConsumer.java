package nu.aron.nextbuildnumber;

import org.apache.maven.shared.utils.cli.CommandLineUtils.StringStreamConsumer;

import static nu.aron.nextbuildnumber.Constants.log;

class LoggingConsumer extends StringStreamConsumer {
    @Override
    public void consumeLine(String line) {
        log.info("This was the line {}", line);
        log.info("This was the size {}", line.length());
        log.error("{}", line);
    }
}
