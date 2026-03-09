package mapsaroundyou.service;

import mapsaroundyou.model.RentalListing;

import java.util.List;

/**
 * Applies listing-level filters before commute lookup.
 */
public class ListingFilter {
    public List<RentalListing> filterByRent(List<RentalListing> listings, int maxRent) {
        return listings.stream()
                .filter(listing -> listing.monthlyRent() <= maxRent)
                .toList();
    }

    public List<RentalListing> filterByAircon(List<RentalListing> listings, boolean requireAircon) {
        if (!requireAircon) {
            return List.copyOf(listings);
        }
        return listings.stream()
                .filter(RentalListing::hasAircon)
                .toList();
    }
}
