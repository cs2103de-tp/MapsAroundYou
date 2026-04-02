package mapsaroundyou.gui;

import mapsaroundyou.common.InvalidInputException;
import mapsaroundyou.model.Destination;
import mapsaroundyou.model.SortMode;
import mapsaroundyou.model.TransportMode;

import java.util.Objects;

/**
 * Converts raw UI input into validated {@link SearchRequest} values.
 */
public final class GuiSearchRequestParser {
    private GuiSearchRequestParser() {
    }

    public static SearchRequest parse(
            Destination destination,
            String maxRentRaw,
            String maxCommuteRaw,
            String maxWalkRaw,
            boolean requireAircon,
            String resultLimitRaw,
            SortMode sortMode,
            boolean excludeWalkDominantRoutes
    ) {
        Objects.requireNonNull(destination, "destination");
        Objects.requireNonNull(sortMode, "sortMode");

        return new SearchRequest(
                destination.destinationId(),
                parseInt(maxRentRaw, "Max rent", 0),
                parseInt(maxCommuteRaw, "Max commute", 1),
                parseInt(maxWalkRaw, "Max walk", 0),
                requireAircon,
                TransportMode.PUBLIC_TRANSPORT,
                parseInt(resultLimitRaw, "Result limit", 1),
                sortMode,
                excludeWalkDominantRoutes
        );
    }

    private static int parseInt(String raw, String label, int minimumValue) {
        if (raw == null || raw.isBlank()) {
            throw new InvalidInputException(label + " is required.");
        }

        int value;
        try {
            value = Integer.parseInt(raw.trim());
        } catch (NumberFormatException exception) {
            throw new InvalidInputException(label + " must be a valid integer.");
        }

        if (value < minimumValue) {
            throw new InvalidInputException(label + " must be at least " + minimumValue + ".");
        }
        return value;
    }
}
