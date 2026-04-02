package mapsaroundyou.logic;

import mapsaroundyou.model.CommuteEstimate;
import mapsaroundyou.model.DatasetMetadata;
import mapsaroundyou.model.Destination;
import mapsaroundyou.model.ListingDetails;
import mapsaroundyou.model.SearchResult;
import mapsaroundyou.model.UserPreferences;

import java.util.List;

public interface SearchLogic {
    List<Destination> getSupportedDestinations();

    DatasetMetadata getDatasetMetadata();

    void updatePreferences(UserPreferences preferences);

    List<SearchResult> generateShortlist();

    ListingDetails getListingDetails(String listingId);

    CommuteEstimate getCommuteDetails(String listingId);

    UserPreferences getCurrentPreferences();
}
