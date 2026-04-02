package mapsaroundyou.common;

import mapsaroundyou.model.SortMode;
import mapsaroundyou.model.TransportMode;
import mapsaroundyou.model.UserPreferences;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Shared application defaults for the Week 8 scaffold.
 */
public final class AppConfig {
    public static final String DESTINATIONS_RESOURCE = "commute_data/Dst_List.csv";
    public static final String ORIGIN_NODES_RESOURCE = "commute_data/origin_nodes.csv";
    public static final String TRAVEL_TIMES_RESOURCE = "commute_data/transit_matrix.csv";
    public static final String LISTINGS_RESOURCE = "commute_data/listings.csv";
    public static final String DATASET_METADATA_RESOURCE = "commute_data/dataset-metadata.properties";

    public static final int DEFAULT_MAX_COMMUTE_MINUTES = 60;
    public static final int DEFAULT_MAX_WALK_MINUTES = 10;
    public static final int DEFAULT_RESULT_LIMIT = 10;
    public static final double DEFAULT_WALK_DOMINANT_THRESHOLD = 0.6d;
    public static final TransportMode DEFAULT_TRANSPORT_MODE = TransportMode.PUBLIC_TRANSPORT;
    public static final SortMode DEFAULT_SORT_MODE = SortMode.BALANCED;

    private AppConfig() {
    }

    public static UserPreferences defaultUserPreferences() {
        return new UserPreferences(
                null,
                0,
                DEFAULT_MAX_COMMUTE_MINUTES,
                DEFAULT_MAX_WALK_MINUTES,
                false,
                DEFAULT_TRANSPORT_MODE,
                DEFAULT_RESULT_LIMIT,
                DEFAULT_SORT_MODE,
                false
        );
    }

    public static Path userPreferencesPath() {
        return Paths.get(System.getProperty("user.home"), ".mapsaroundyou", "user-preferences.properties");
    }
}
