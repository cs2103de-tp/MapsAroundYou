package mapsaroundyou.common;

/**
 * Raised when CLI or logic input fails validation.
 */
public class InvalidInputException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidInputException(String message) {
        super(message);
    }
}
