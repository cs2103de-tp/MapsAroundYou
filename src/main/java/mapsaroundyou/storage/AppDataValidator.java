package mapsaroundyou.storage;

import mapsaroundyou.common.DataLoadException;
import mapsaroundyou.model.Destination;
import mapsaroundyou.model.RentalListing;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Cross-dataset validation that runs during application startup.
 */
public final class AppDataValidator {
    private AppDataValidator() {
    }

    public static void validate(
            OriginNodeRepository originNodeRepository,
            DestinationRepository destinationRepository,
            ListingRepository listingRepository,
            TravelTimeRepository travelTimeRepository
    ) {
        Set<String> knownOriginIds = originNodeRepository.findAll().stream()
                .map(originNode -> originNode.originNodeId())
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
        Set<String> knownDestinationIds = destinationRepository.findAll().stream()
                .map(Destination::destinationId)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
        List<RentalListing> listings = listingRepository.findAll();
        Set<String> knownTravelTimeOrigins = travelTimeRepository.findKnownOrigins();
        Set<String> knownTravelTimeDestinations = travelTimeRepository.findKnownDestinations();
        Map<String, Set<String>> knownDestinationsByOrigin = travelTimeRepository.findKnownDestinationsByOrigin();

        for (RentalListing listing : listings) {
            if (!knownOriginIds.contains(listing.originNodeId())) {
                throw new DataLoadException("Listing " + listing.listingId()
                        + " references unknown origin node " + listing.originNodeId());
            }
            if (!knownTravelTimeOrigins.contains(listing.originNodeId())) {
                throw new DataLoadException("Listing " + listing.listingId()
                        + " has no travel-time records for origin " + listing.originNodeId());
            }
        }

        for (String originId : knownTravelTimeOrigins) {
            if (!knownOriginIds.contains(originId)) {
                throw new DataLoadException("Travel-time dataset references unknown origin node " + originId);
            }
        }

        for (String destinationId : knownTravelTimeDestinations) {
            if (!knownDestinationIds.contains(destinationId)) {
                throw new DataLoadException("Travel-time dataset references unknown destination " + destinationId);
            }
        }

        for (RentalListing listing : listings) {
            Set<String> destinationsForOrigin = knownDestinationsByOrigin.get(listing.originNodeId());
            if (destinationsForOrigin == null || !destinationsForOrigin.containsAll(knownDestinationIds)) {
                throw new DataLoadException("Missing travel-time coverage for listing origin "
                        + listing.originNodeId());
            }
        }
    }
}
