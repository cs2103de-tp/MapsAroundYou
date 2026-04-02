package mapsaroundyou.logic;

import mapsaroundyou.model.CommuteEstimate;
import mapsaroundyou.model.DatasetMetadata;
import mapsaroundyou.model.Destination;
import mapsaroundyou.model.ListingDetails;
import mapsaroundyou.model.SearchResult;
import mapsaroundyou.model.UserPreferences;
import mapsaroundyou.storage.UserPrefsRepository;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Decorates {@link SearchLogic} with local preference persistence.
 */
public class PersistentSearchLogic implements SearchLogic {
    private final SearchLogic delegate;
    private final UserPrefsRepository userPrefsRepository;

    public PersistentSearchLogic(SearchLogic delegate, UserPrefsRepository userPrefsRepository) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        this.userPrefsRepository = Objects.requireNonNull(userPrefsRepository, "userPrefsRepository");
        try {
            delegate.updatePreferences(sanitizeLoadedPreferences(userPrefsRepository.load()));
        } catch (RuntimeException exception) {
            // Ignore invalid persisted state and keep the delegate defaults.
        }
    }

    @Override
    public List<Destination> getSupportedDestinations() {
        return delegate.getSupportedDestinations();
    }

    @Override
    public DatasetMetadata getDatasetMetadata() {
        return delegate.getDatasetMetadata();
    }

    @Override
    public void updatePreferences(UserPreferences preferences) {
        delegate.updatePreferences(preferences);
    }

    @Override
    public List<SearchResult> generateShortlist() {
        List<SearchResult> results = delegate.generateShortlist();
        try {
            userPrefsRepository.save(delegate.getCurrentPreferences());
        } catch (RuntimeException exception) {
            // Preference persistence is best-effort and must not block search.
        }
        return results;
    }

    @Override
    public ListingDetails getListingDetails(String listingId) {
        return delegate.getListingDetails(listingId);
    }

    @Override
    public CommuteEstimate getCommuteDetails(String listingId) {
        return delegate.getCommuteDetails(listingId);
    }

    @Override
    public UserPreferences getCurrentPreferences() {
        return delegate.getCurrentPreferences();
    }

    private UserPreferences sanitizeLoadedPreferences(UserPreferences loadedPreferences) {
        UserPreferences preferences = loadedPreferences == null ? UserPreferences.defaults() : loadedPreferences;
        if (preferences.destinationId() == null || preferences.destinationId().isBlank()) {
            return preferences.withDestination(null);
        }

        Set<String> supportedDestinationIds = delegate.getSupportedDestinations().stream()
                .map(Destination::destinationId)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
        if (supportedDestinationIds.contains(preferences.destinationId())) {
            return preferences;
        }
        return preferences.withDestination(null);
    }
}
