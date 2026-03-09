package mapsaroundyou.service;

import mapsaroundyou.common.DataLoadException;
import mapsaroundyou.model.CommuteEstimate;
import mapsaroundyou.model.TransportMode;
import mapsaroundyou.storage.TravelTimeRepository;

/**
 * Looks up commute estimates from the local travel-time matrix.
 */
public class CommuteEstimator {
    private final TravelTimeRepository travelTimeRepository;

    public CommuteEstimator(TravelTimeRepository travelTimeRepository) {
        this.travelTimeRepository = travelTimeRepository;
    }

    public CommuteEstimate estimate(String originNodeId, String destinationId, TransportMode transportMode) {
        if (transportMode != TransportMode.PUBLIC_TRANSPORT) {
            throw new DataLoadException("Unsupported transport mode: " + transportMode);
        }
        return travelTimeRepository.findByOriginAndDestination(originNodeId, destinationId)
                .orElseThrow(() -> new DataLoadException(
                        "No commute record for origin " + originNodeId + " and destination " + destinationId));
    }
}
