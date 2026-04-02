package mapsaroundyou.cli;

import mapsaroundyou.model.SortMode;

record SearchCommandArguments(
        String destinationId,
        int maxRent,
        int maxCommuteMinutes,
        Integer maxWalkMinutes,
        boolean requireAircon,
        Integer resultLimit,
        SortMode sortMode,
        boolean excludeWalkDominantRoutes
) {
}
