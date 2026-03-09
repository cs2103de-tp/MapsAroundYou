package mapsaroundyou.model;

/**
 * Current search preferences.
 */
public record UserPreferences(
        String destinationId,
        int maxRent,
        int maxCommuteMinutes,
        boolean requireAircon,
        TransportMode transportMode,
        int resultLimit,
        boolean excludeWalkDominantRoutes
) {
}
