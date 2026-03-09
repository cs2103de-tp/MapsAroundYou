package mapsaroundyou.service;

import mapsaroundyou.model.CommuteEstimate;

/**
 * Placeholder for V1.4 route sanity checks and summaries.
 */
public class RouteAnalyzer {
    private final double walkDominantThreshold;

    public RouteAnalyzer(double walkDominantThreshold) {
        this.walkDominantThreshold = walkDominantThreshold;
    }

    public boolean isWalkDominant(CommuteEstimate commuteEstimate) {
        if (commuteEstimate.totalMinutes() == 0) {
            return false;
        }
        return ((double) commuteEstimate.walkMinutes() / commuteEstimate.totalMinutes()) >= walkDominantThreshold;
    }

    public String summarize(CommuteEstimate commuteEstimate) {
        return String.format(
                "Total %d min (%d min transit, %d min walk, fare SGD %.2f)",
                commuteEstimate.totalMinutes(),
                commuteEstimate.transitMinutes(),
                commuteEstimate.walkMinutes(),
                commuteEstimate.fare()
        );
    }
}
