package mapsaroundyou.logic;

import mapsaroundyou.model.CommuteEstimate;
import mapsaroundyou.model.DatasetMetadata;
import mapsaroundyou.model.Destination;
import mapsaroundyou.model.ListingDetails;
import mapsaroundyou.model.SearchResult;
import mapsaroundyou.model.TransportMode;
import mapsaroundyou.model.UserPreferences;

import java.util.List;

public interface SearchLogic {
    List<Destination> getSupportedDestinations();

    DatasetMetadata getDatasetMetadata();

    void setDestination(String destinationId);

        void setPreferences(
            int maxRent,
            int maxCommuteMinutes,
            int maxTransfers,
            boolean requireAircon,
            TransportMode transportMode
        );

    List<SearchResult> generateShortlist();

    ListingDetails getListingDetails(String listingId);

    CommuteEstimate getCommuteDetails(String listingId);

    UserPreferences getCurrentPreferences();
}
