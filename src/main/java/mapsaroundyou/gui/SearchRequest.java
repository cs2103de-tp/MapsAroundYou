package mapsaroundyou.gui;

import mapsaroundyou.model.SortMode;
import mapsaroundyou.model.TransportMode;
import mapsaroundyou.model.UserPreferences;

public record SearchRequest(
        String destinationId,
        int maxRent,
        int maxCommuteMinutes,
        int maxWalkMinutes,
        boolean requireAircon,
        TransportMode transportMode,
        int resultLimit,
        SortMode sortMode,
        boolean excludeWalkDominantRoutes
) {
    public UserPreferences toUserPreferences() {
        return new UserPreferences(
                destinationId,
                maxRent,
                maxCommuteMinutes,
                maxWalkMinutes,
                requireAircon,
                transportMode,
                resultLimit,
                sortMode,
                excludeWalkDominantRoutes
        );
    }
}

