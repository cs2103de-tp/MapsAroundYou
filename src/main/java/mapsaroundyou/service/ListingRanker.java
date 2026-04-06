package mapsaroundyou.service;

import mapsaroundyou.model.SearchResult;
import mapsaroundyou.model.SortMode;

import java.util.Comparator;
import java.util.List;

/**
 * Produces deterministic ranking for shortlisted results.
 */
public class ListingRanker {
    private static final double COMMUTE_SCORE_WEIGHT = 0.5d;
    private static final double RENT_SCORE_WEIGHT = 0.5d;

    private static final Comparator<SearchResult> COMMUTE_COMPARATOR = Comparator
            .comparingInt((SearchResult result) -> result.commute().totalMinutes())
            .thenComparingInt(result -> result.listing().monthlyRent())
            .thenComparing(result -> result.listing().listingId());
    private static final Comparator<SearchResult> RENT_COMPARATOR = Comparator
            .comparingInt((SearchResult result) -> result.listing().monthlyRent())
            .thenComparingInt(result -> result.commute().totalMinutes())
            .thenComparing(result -> result.listing().listingId());
    private static final Comparator<SearchResult> BALANCED_COMPARATOR = Comparator
            .comparingDouble(SearchResult::score)
            .reversed()
            .thenComparingInt(result -> result.commute().totalMinutes())
            .thenComparingInt(result -> result.listing().monthlyRent())
            .thenComparing(result -> result.listing().listingId());

    public List<SearchResult> rank(List<SearchResult> results, SortMode sortMode) {
        Comparator<SearchResult> comparator = switch (sortMode) {
        case COMMUTE -> COMMUTE_COMPARATOR;
        case RENT -> RENT_COMPARATOR;
        case BALANCED -> BALANCED_COMPARATOR;
        };
        return results.stream()
                .sorted(comparator)
                .toList();
    }

    public double computeScore(SearchResult result, int maxRent, int maxCommuteMinutes) {
        double normalizedCommute = (double) result.commute().totalMinutes() / maxCommuteMinutes;
        double normalizedRent = (double) result.listing().monthlyRent() / maxRent;
        double rawScore = 1.0d - ((COMMUTE_SCORE_WEIGHT * normalizedCommute)
                + (RENT_SCORE_WEIGHT * normalizedRent));
        return Math.max(0.0d, rawScore);
    }
}
