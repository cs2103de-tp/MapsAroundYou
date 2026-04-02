package mapsaroundyou.gui;

import mapsaroundyou.model.TransportMode;

public record SearchRequest(
        String destinationId,
        int maxRent,
        int maxCommuteMinutes,
        boolean requireAircon,
        TransportMode transportMode
) {
}

