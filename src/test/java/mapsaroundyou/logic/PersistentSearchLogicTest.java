package mapsaroundyou.logic;

import mapsaroundyou.model.CommuteEstimate;
import mapsaroundyou.model.DatasetMetadata;
import mapsaroundyou.model.Destination;
import mapsaroundyou.model.ListingDetails;
import mapsaroundyou.model.SearchResult;
import mapsaroundyou.model.SortMode;
import mapsaroundyou.model.TransportMode;
import mapsaroundyou.model.UserPreferences;
import mapsaroundyou.storage.UserPrefsRepository;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PersistentSearchLogicTest {
    @Test
    void constructor_loadsSavedPreferencesAndClearsUnknownDestination() {
        UserPreferences savedPreferences = new UserPreferences(
                "UNKNOWN",
                2300,
                55,
                12,
                true,
                TransportMode.PUBLIC_TRANSPORT,
                7,
                SortMode.RENT,
                true
        );
        InMemoryUserPrefsRepository repository = new InMemoryUserPrefsRepository(savedPreferences);
        RecordingSearchLogic delegate = new RecordingSearchLogic();

        PersistentSearchLogic persistentSearchLogic = new PersistentSearchLogic(delegate, repository);

        UserPreferences currentPreferences = persistentSearchLogic.getCurrentPreferences();
        assertNull(currentPreferences.destinationId());
        assertEquals(2300, currentPreferences.maxRent());
        assertEquals(55, currentPreferences.maxCommuteMinutes());
        assertEquals(12, currentPreferences.maxWalkMinutes());
        assertEquals(7, currentPreferences.resultLimit());
        assertEquals(SortMode.RENT, currentPreferences.sortMode());
        assertEquals(true, currentPreferences.excludeWalkDominantRoutes());
    }

    @Test
    void generateShortlist_savesCurrentPreferencesAfterSuccessfulSearch() {
        InMemoryUserPrefsRepository repository = new InMemoryUserPrefsRepository(UserPreferences.defaults());
        RecordingSearchLogic delegate = new RecordingSearchLogic();
        PersistentSearchLogic persistentSearchLogic = new PersistentSearchLogic(delegate, repository);

        UserPreferences updatedPreferences = new UserPreferences(
                "D01",
                2000,
                45,
                10,
                false,
                TransportMode.PUBLIC_TRANSPORT,
                5,
                SortMode.BALANCED,
                true
        );
        persistentSearchLogic.updatePreferences(updatedPreferences);
        persistentSearchLogic.generateShortlist();

        assertEquals(updatedPreferences, repository.savedPreferences);
    }

    private static final class InMemoryUserPrefsRepository implements UserPrefsRepository {
        private final UserPreferences loadedPreferences;
        private UserPreferences savedPreferences;

        private InMemoryUserPrefsRepository(UserPreferences loadedPreferences) {
            this.loadedPreferences = loadedPreferences;
        }

        @Override
        public UserPreferences load() {
            return loadedPreferences;
        }

        @Override
        public void save(UserPreferences preferences) {
            savedPreferences = preferences;
        }
    }

    private static final class RecordingSearchLogic implements SearchLogic {
        private UserPreferences currentPreferences = UserPreferences.defaults();

        @Override
        public List<Destination> getSupportedDestinations() {
            return List.of(new Destination("D01", "NUS", "University", "Kent Ridge", "117575"));
        }

        @Override
        public DatasetMetadata getDatasetMetadata() {
            return new DatasetMetadata(LocalDate.of(2026, 3, 8), "Fixture dataset");
        }

        @Override
        public void updatePreferences(UserPreferences preferences) {
            currentPreferences = preferences;
        }

        @Override
        public List<SearchResult> generateShortlist() {
            return List.of();
        }

        @Override
        public ListingDetails getListingDetails(String listingId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public CommuteEstimate getCommuteDetails(String listingId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public UserPreferences getCurrentPreferences() {
            return currentPreferences;
        }
    }
}
