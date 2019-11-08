package nu.aron.nextbuildnumber;

class PluginException extends RuntimeException {
    Exception e;

    PluginException(Exception e) {
        this.e = e;
    }
}
