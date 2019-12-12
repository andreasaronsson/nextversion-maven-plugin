package nu.aron.nextbuildnumber;

class PluginException extends RuntimeException {
    Throwable e;

    PluginException(Throwable e) {
        this.e = e;
    }
}
