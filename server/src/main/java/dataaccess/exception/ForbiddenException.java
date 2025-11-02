package dataaccess.exception;

/**
 * Indicates the request was forbidden
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
