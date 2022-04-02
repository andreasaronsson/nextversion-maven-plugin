package nu.aron.next;

class PluginException extends RuntimeException {
    final Throwable throwable;
    final String message;

    PluginException(Throwable t) {
        throwable = t;
        message = t.getMessage();
    }
}
