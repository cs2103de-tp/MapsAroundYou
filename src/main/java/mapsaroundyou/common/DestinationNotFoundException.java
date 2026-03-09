package mapsaroundyou.common;

/**
 * Raised when a destination id is not part of the supported dataset.
 */
public class DestinationNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DestinationNotFoundException(String message) {
        super(message);
    }
}
