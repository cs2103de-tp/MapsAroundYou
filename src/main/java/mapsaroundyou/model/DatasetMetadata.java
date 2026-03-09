package mapsaroundyou.model;

import java.time.LocalDate;

/**
 * Dataset freshness metadata for later UI display.
 */
public record DatasetMetadata(LocalDate lastUpdated, String sourceDescription) {
}
