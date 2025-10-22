package dataaccess;

/**
 * Indicates the auth token is invalid
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
