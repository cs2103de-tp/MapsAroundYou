package mapsaroundyou.storage;

import mapsaroundyou.common.DataLoadException;
import mapsaroundyou.model.SortMode;
import mapsaroundyou.model.TransportMode;
import mapsaroundyou.model.UserPreferences;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;

/**
 * Stores last-used preferences in a simple local properties file.
 */
public class PropertiesUserPrefsRepository implements UserPrefsRepository {
    private static final String DESTINATION_ID_KEY = "destinationId";
    private static final String MAX_RENT_KEY = "maxRent";
    private static final String MAX_COMMUTE_MINUTES_KEY = "maxCommuteMinutes";
    private static final String MAX_WALK_MINUTES_KEY = "maxWalkMinutes";
    private static final String REQUIRE_AIRCON_KEY = "requireAircon";
    private static final String TRANSPORT_MODE_KEY = "transportMode";
    private static final String RESULT_LIMIT_KEY = "resultLimit";
    private static final String SORT_MODE_KEY = "sortMode";
    private static final String EXCLUDE_WALK_DOMINANT_KEY = "excludeWalkDominantRoutes";

    private final Path preferencesPath;

    public PropertiesUserPrefsRepository(Path preferencesPath) {
        this.preferencesPath = preferencesPath;
    }

    @Override
    public UserPreferences load() {
        if (!Files.exists(preferencesPath)) {
            return UserPreferences.defaults();
        }

        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(preferencesPath)) {
            properties.load(inputStream);
        } catch (IOException exception) {
            return UserPreferences.defaults();
        }

        UserPreferences defaults = UserPreferences.defaults();
        return new UserPreferences(
                normalizeDestinationId(properties.getProperty(DESTINATION_ID_KEY)),
                parseInt(properties.getProperty(MAX_RENT_KEY), defaults.maxRent(), 0),
                parseInt(properties.getProperty(MAX_COMMUTE_MINUTES_KEY), defaults.maxCommuteMinutes(), 1),
                parseInt(properties.getProperty(MAX_WALK_MINUTES_KEY), defaults.maxWalkMinutes(), 0),
                parseBoolean(properties.getProperty(REQUIRE_AIRCON_KEY), defaults.requireAircon()),
                parseTransportMode(properties.getProperty(TRANSPORT_MODE_KEY), defaults.transportMode()),
                parseInt(properties.getProperty(RESULT_LIMIT_KEY), defaults.resultLimit(), 1),
                parseSortMode(properties.getProperty(SORT_MODE_KEY), defaults.sortMode()),
                parseBoolean(
                        properties.getProperty(EXCLUDE_WALK_DOMINANT_KEY),
                        defaults.excludeWalkDominantRoutes()
                )
        );
    }

    @Override
    public void save(UserPreferences preferences) {
        Properties properties = new Properties();
        if (preferences.destinationId() != null && !preferences.destinationId().isBlank()) {
            properties.setProperty(DESTINATION_ID_KEY, preferences.destinationId());
        }
        properties.setProperty(MAX_RENT_KEY, Integer.toString(preferences.maxRent()));
        properties.setProperty(MAX_COMMUTE_MINUTES_KEY, Integer.toString(preferences.maxCommuteMinutes()));
        properties.setProperty(MAX_WALK_MINUTES_KEY, Integer.toString(preferences.maxWalkMinutes()));
        properties.setProperty(REQUIRE_AIRCON_KEY, Boolean.toString(preferences.requireAircon()));
        properties.setProperty(TRANSPORT_MODE_KEY, preferences.transportMode().name());
        properties.setProperty(RESULT_LIMIT_KEY, Integer.toString(preferences.resultLimit()));
        properties.setProperty(SORT_MODE_KEY, preferences.sortMode().name());
        properties.setProperty(EXCLUDE_WALK_DOMINANT_KEY, Boolean.toString(preferences.excludeWalkDominantRoutes()));

        try {
            Path parentDirectory = preferencesPath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            try (OutputStream outputStream = Files.newOutputStream(preferencesPath)) {
                properties.store(outputStream, "MapsAroundYou user preferences");
            }
        } catch (IOException exception) {
            throw new DataLoadException("Failed to save user preferences.", exception);
        }
    }

    private static String normalizeDestinationId(String destinationId) {
        if (destinationId == null || destinationId.isBlank()) {
            return null;
        }
        return destinationId.trim();
    }

    private static int parseInt(String rawValue, int fallbackValue, int minimumValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return fallbackValue;
        }
        try {
            int parsedValue = Integer.parseInt(rawValue.trim());
            return parsedValue >= minimumValue ? parsedValue : fallbackValue;
        } catch (NumberFormatException exception) {
            return fallbackValue;
        }
    }

    private static boolean parseBoolean(String rawValue, boolean fallbackValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return fallbackValue;
        }
        String normalized = rawValue.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
        case "true" -> true;
        case "false" -> false;
        default -> fallbackValue;
        };
    }

    private static TransportMode parseTransportMode(String rawValue, TransportMode fallbackValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return fallbackValue;
        }
        try {
            return TransportMode.valueOf(rawValue.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return fallbackValue;
        }
    }

    private static SortMode parseSortMode(String rawValue, SortMode fallbackValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return fallbackValue;
        }
        try {
            return SortMode.valueOf(rawValue.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return fallbackValue;
        }
    }
}
