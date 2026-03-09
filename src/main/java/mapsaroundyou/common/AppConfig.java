package mapsaroundyou.common;

import mapsaroundyou.model.TransportMode;

/**
 * Shared application defaults for the Week 8 scaffold.
 */
public final class AppConfig {
    public static final String DESTINATIONS_RESOURCE = "commute_data/Dst_List.csv";
    public static final String ORIGIN_NODES_RESOURCE = "commute_data/Rental_List.csv";
    public static final String TRAVEL_TIMES_RESOURCE = "commute_data/transit_matrix.csv";
    public static final String LISTINGS_RESOURCE = "commute_data/listings.csv";
    public static final String DATASET_METADATA_RESOURCE = "commute_data/dataset-metadata.properties";

    public static final int DEFAULT_RESULT_LIMIT = 10;
    public static final double DEFAULT_WALK_DOMINANT_THRESHOLD = 0.6d;
    public static final TransportMode DEFAULT_TRANSPORT_MODE = TransportMode.PUBLIC_TRANSPORT;

    private AppConfig() {
    }
}
