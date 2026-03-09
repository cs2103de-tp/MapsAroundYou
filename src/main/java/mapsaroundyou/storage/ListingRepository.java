package mapsaroundyou.storage;

import mapsaroundyou.model.RentalListing;

import java.util.List;
import java.util.Optional;

public interface ListingRepository {
    List<RentalListing> findAll();

    Optional<RentalListing> findById(String listingId);
}
