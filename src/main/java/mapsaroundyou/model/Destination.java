package mapsaroundyou.model;

/**
 * Supported destination for commute searches.
 */
public record Destination(
        String destinationId,
        String name,
        String category,
        String area,
        String postalCode
) {
}
