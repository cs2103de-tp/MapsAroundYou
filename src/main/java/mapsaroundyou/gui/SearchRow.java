package mapsaroundyou.gui;

import mapsaroundyou.model.CommuteEstimate;
import mapsaroundyou.model.RentalListing;
import mapsaroundyou.model.SearchResult;

import java.util.Objects;

/**
 * UI-friendly view of {@link SearchResult} for TableView display.
 */
public final class SearchRow {
    private final SearchResult result;

    public SearchRow(SearchResult result) {
        this.result = Objects.requireNonNull(result, "result");
    }

    public SearchResult result() {
        return result;
    }

    public String getListingId() {
        return listing().listingId();
    }

    public String getTitle() {
        return listing().title();
    }

    public int getMonthlyRent() {
        return listing().monthlyRent();
    }

    public boolean hasAircon() {
        return listing().hasAircon();
    }

    public int getTotalCommuteMinutes() {
        return commute().totalMinutes();
    }

    public double getScore() {
        return result.score();
    }

    public CommuteEstimate commute() {
        return result.commute();
    }

    public RentalListing listing() {
        return result.listing();
    }
}

