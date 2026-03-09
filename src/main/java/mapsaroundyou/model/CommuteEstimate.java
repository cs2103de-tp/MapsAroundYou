package mapsaroundyou.model;

/**
 * Commute estimate derived from the offline transit matrix.
 */
public record CommuteEstimate(
        String originNodeId,
        String destinationId,
        int totalMinutes,
        int transitMinutes,
        int walkMinutes,
        int transfers,
        double fare
) {
}
