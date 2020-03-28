package nu.aron.nextbuildnumber;

class PluginException extends RuntimeException {
    final Throwable e;

    PluginException(Throwable e) {
        this.e = e;
    }
}
