package mapsaroundyou.gui;

import mapsaroundyou.logic.SearchLogic;
import mapsaroundyou.model.DatasetMetadata;
import mapsaroundyou.model.Destination;
import mapsaroundyou.model.ListingDetails;
import mapsaroundyou.model.SearchResult;
import mapsaroundyou.model.UserPreferences;

import java.util.List;
import java.util.Objects;

/**
 * Thin GUI-facing facade that hides the stateful {@link SearchLogic} call order.
 */
public final class GuiSearchService {
    private final SearchLogic searchLogic;

    public GuiSearchService(SearchLogic searchLogic) {
        this.searchLogic = Objects.requireNonNull(searchLogic, "searchLogic");
    }

    public List<Destination> getSupportedDestinations() {
        return searchLogic.getSupportedDestinations();
    }

    public DatasetMetadata getDatasetMetadata() {
        return searchLogic.getDatasetMetadata();
    }

    public SearchResponse search(SearchRequest request) {
        Objects.requireNonNull(request, "request");
        searchLogic.updatePreferences(request.toUserPreferences());
        List<SearchResult> results = searchLogic.generateShortlist();
        return new SearchResponse(searchLogic.getDatasetMetadata(), results);
    }

    public ListingDetails getListingDetails(String listingId) {
        return searchLogic.getListingDetails(listingId);
    }

    public UserPreferences getCurrentPreferences() {
        return searchLogic.getCurrentPreferences();
    }
}

