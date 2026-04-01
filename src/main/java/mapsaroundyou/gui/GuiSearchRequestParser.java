package mapsaroundyou.gui;

import mapsaroundyou.common.InvalidInputException;
import mapsaroundyou.model.Destination;
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
            boolean requireAircon
    ) {
        Objects.requireNonNull(destination, "destination");

        return new SearchRequest(
                destination.destinationId(),
                parseInt(maxRentRaw, "Max rent"),
                parseInt(maxCommuteRaw, "Max commute"),
                requireAircon,
                TransportMode.PUBLIC_TRANSPORT
        );
    }

    private static int parseInt(String raw, String label) {
        if (raw == null || raw.isBlank()) {
            throw new InvalidInputException(label + " is required.");
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException exception) {
            throw new InvalidInputException(label + " must be a valid integer.");
        }
    }
}
