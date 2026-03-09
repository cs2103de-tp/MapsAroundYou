package mapsaroundyou.service;

import mapsaroundyou.model.RentalListing;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListingFilterTest {
    private final ListingFilter listingFilter = new ListingFilter();

    @Test
    void filterByRent_andAircon_returnsExpectedListings() {
        List<RentalListing> listings = List.of(
                new RentalListing("L001", "A", 1400, true, "R01", "Addr 1", "HDB", "PG", "Note"),
                new RentalListing("L002", "B", 1800, false, "R02", "Addr 2", "HDB", "PG", "Note"),
                new RentalListing("L003", "C", 1600, true, "R03", "Addr 3", "HDB", "PG", "Note")
        );

        List<RentalListing> byRent = listingFilter.filterByRent(listings, 1600);
        List<RentalListing> byRentAndAircon = listingFilter.filterByAircon(byRent, true);

        assertEquals(List.of("L001", "L003"), byRent.stream().map(RentalListing::listingId).toList());
        assertEquals(List.of("L001", "L003"), byRentAndAircon.stream().map(RentalListing::listingId).toList());
    }
}
