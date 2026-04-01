package mapsaroundyou.gui;

import mapsaroundyou.model.CommuteEstimate;
import mapsaroundyou.model.DatasetMetadata;
import mapsaroundyou.model.Destination;

import java.time.format.DateTimeFormatter;

/**
 * Shared text-formatting helpers used by the GUI layer.
 */
public final class GuiTextFormatter {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private GuiTextFormatter() {
    }

    public static String formatDestination(Destination destination) {
        if (destination == null) {
            return "";
        }

        String area = destination.area() == null ? "" : destination.area().trim();
        String category = destination.category() == null ? "" : destination.category().trim();
        String suffix = "";
        if (!category.isBlank() && !area.isBlank()) {
            suffix = " (" + category + " • " + area + ")";
        } else if (!category.isBlank()) {
            suffix = " (" + category + ")";
        } else if (!area.isBlank()) {
            suffix = " (" + area + ")";
        }
        return destination.name() + suffix;
    }

    public static String formatDatasetMetadata(DatasetMetadata metadata) {
        if (metadata == null) {
            return "";
        }

        String updated = metadata.lastUpdated() == null ? "unknown" : metadata.lastUpdated().format(DATE_FORMAT);
        String source = metadata.sourceDescription() == null ? "" : metadata.sourceDescription().trim();
        if (source.isBlank()) {
            return "Dataset last updated: " + updated;
        }
        return "Dataset last updated: " + updated + " • " + source;
    }

    public static String formatCommute(CommuteEstimate commute) {
        return commute.totalMinutes()
                + " min total ("
                + commute.transitMinutes() + " transit, "
                + commute.walkMinutes() + " walk, "
                + commute.transfers() + " transfer(s), "
                + String.format("$%.2f", commute.fare())
                + ")";
    }

    public static String formatOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return "-";
        }
        return value;
    }
}
