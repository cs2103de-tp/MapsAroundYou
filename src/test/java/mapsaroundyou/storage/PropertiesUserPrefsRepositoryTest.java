package mapsaroundyou.storage;

import mapsaroundyou.model.SortMode;
import mapsaroundyou.model.TransportMode;
import mapsaroundyou.model.UserPreferences;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PropertiesUserPrefsRepositoryTest {
    @TempDir
    Path tempDir;

    @Test
    void saveThenLoad_roundTripsAllFields() {
        Path prefsFile = tempDir.resolve("user-preferences.properties");
        PropertiesUserPrefsRepository repository = new PropertiesUserPrefsRepository(prefsFile);
        UserPreferences preferences = new UserPreferences(
                "D01",
                2200,
                45,
                10,
                true,
                TransportMode.PUBLIC_TRANSPORT,
                5,
                SortMode.BALANCED,
                true
        );

        repository.save(preferences);

        assertEquals(preferences, repository.load());
    }

    @Test
    void load_missingFile_returnsDefaults() {
        PropertiesUserPrefsRepository repository = new PropertiesUserPrefsRepository(
                tempDir.resolve("missing.properties"));

        assertEquals(UserPreferences.defaults(), repository.load());
    }

    @Test
    void load_invalidValues_fallsBackFieldByField() throws Exception {
        Path prefsFile = tempDir.resolve("invalid.properties");
        java.nio.file.Files.writeString(prefsFile, String.join(System.lineSeparator(),
                "destinationId=D99",
                "maxRent=oops",
                "maxCommuteMinutes=60",
                "maxWalkMinutes=bad",
                "requireAircon=true",
                "transportMode=PUBLIC_TRANSPORT",
                "resultLimit=0",
                "sortMode=FAST",
                "excludeWalkDominantRoutes=true"
        ));
        PropertiesUserPrefsRepository repository = new PropertiesUserPrefsRepository(prefsFile);

        UserPreferences loadedPreferences = repository.load();

        assertEquals("D99", loadedPreferences.destinationId());
        assertEquals(UserPreferences.defaults().maxRent(), loadedPreferences.maxRent());
        assertEquals(60, loadedPreferences.maxCommuteMinutes());
        assertEquals(UserPreferences.defaults().maxWalkMinutes(), loadedPreferences.maxWalkMinutes());
        assertEquals(true, loadedPreferences.requireAircon());
        assertEquals(UserPreferences.defaults().resultLimit(), loadedPreferences.resultLimit());
        assertEquals(UserPreferences.defaults().sortMode(), loadedPreferences.sortMode());
        assertEquals(true, loadedPreferences.excludeWalkDominantRoutes());
    }
}
