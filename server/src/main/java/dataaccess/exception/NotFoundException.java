package dataaccess.exception;

/**
 * Indicates the requested data could not be found
 */
public class NotFoundException extends Exception {
    public NotFoundException(String message) {
        super(message);
    }
    public NotFoundException(String message, Throwable ex) {
        super(message, ex);
    }
}
