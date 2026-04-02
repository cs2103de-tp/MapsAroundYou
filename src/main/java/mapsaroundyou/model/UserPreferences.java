package mapsaroundyou.model;

import mapsaroundyou.common.AppConfig;

/**
 * Current search preferences.
 */
public record UserPreferences(
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
    public static UserPreferences defaults() {
        return AppConfig.defaultUserPreferences();
    }

    public UserPreferences withDestination(String updatedDestinationId) {
        return new UserPreferences(
                updatedDestinationId,
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
