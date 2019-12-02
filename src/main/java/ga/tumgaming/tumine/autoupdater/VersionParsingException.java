package ga.tumgaming.tumine.autoupdater;

public class VersionParsingException extends Exception {

    public VersionParsingException() {
    }

    public VersionParsingException(String message) {
        super(message);
    }

    public VersionParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public VersionParsingException(Throwable cause) {
        super(cause);
    }

    public VersionParsingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
