package nu.aron.next;

import static nu.aron.next.Constants.logger;

class LoggingConsumer implements java.util.function.Consumer<String> {
    @Override
    public void accept(String message) {
        logger.error("{}", message);
    }
}
