package mapsaroundyou.common;

/**
 * Raised when a listing id is not present in the local dataset.
 */
public class ListingNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ListingNotFoundException(String message) {
        super(message);
    }
}
