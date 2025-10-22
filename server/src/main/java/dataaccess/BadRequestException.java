package dataaccess;

/**
 * Indicates the request was bad/malformed
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
