package mapsaroundyou.model;

/**
 * Deterministic default values applied when a persona preset is selected in the GUI.
 */
public record PersonaPresetAppliedValues(
        int maxRent,
        int maxCommuteMinutes,
        boolean requireAircon
) {
}

