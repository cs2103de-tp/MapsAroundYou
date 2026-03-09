package mapsaroundyou.model;

/**
 * Ranked listing result returned by the logic layer.
 */
public record SearchResult(RentalListing listing, CommuteEstimate commute, double score) {
}
