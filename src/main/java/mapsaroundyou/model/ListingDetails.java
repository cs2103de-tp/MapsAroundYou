package mapsaroundyou.model;

import java.util.Optional;

/**
 * Detailed listing view model used by later UI work.
 */
public record ListingDetails(RentalListing listing, Optional<CommuteEstimate> commuteEstimate) {
}
