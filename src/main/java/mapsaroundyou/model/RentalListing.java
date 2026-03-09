package mapsaroundyou.model;

/**
 * Curated demo listing used by the CLI scaffold.
 */
public record RentalListing(
        String listingId,
        String title,
        int monthlyRent,
        boolean hasAircon,
        String originNodeId,
        String address,
        String roomType,
        String sourcePlatform,
        String notes
) {
}
