package mapsaroundyou.model;

import java.util.Locale;

/**
 * Supported shortlist sorting strategies.
 */
public enum SortMode {
    COMMUTE("Commute", "commute"),
    RENT("Rent", "rent"),
    BALANCED("Balanced", "balanced");

    private final String displayName;
    private final String cliValue;

    SortMode(String displayName, String cliValue) {
        this.displayName = displayName;
        this.cliValue = cliValue;
    }

    public String cliValue() {
        return cliValue;
    }

    public static SortMode fromCliValue(String rawValue) {
        String normalized = rawValue == null ? "" : rawValue.trim().toLowerCase(Locale.ROOT);
        for (SortMode sortMode : values()) {
            if (sortMode.cliValue.equals(normalized)) {
                return sortMode;
            }
        }
        throw new IllegalArgumentException("Unknown sort mode: " + rawValue);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
