package mapsaroundyou.logic;

import mapsaroundyou.common.AppConfig;
import mapsaroundyou.common.DestinationNotFoundException;
import mapsaroundyou.common.InvalidInputException;
import mapsaroundyou.common.ListingNotFoundException;
import mapsaroundyou.common.NoResultsException;
import mapsaroundyou.model.CommuteEstimate;
import mapsaroundyou.model.DatasetMetadata;
import mapsaroundyou.model.Destination;
import mapsaroundyou.model.ListingDetails;
import mapsaroundyou.model.RentalListing;
import mapsaroundyou.model.SearchResult;
import mapsaroundyou.model.UserPreferences;
import mapsaroundyou.service.CommuteEstimator;
import mapsaroundyou.service.ListingFilter;
import mapsaroundyou.service.ListingRanker;
import mapsaroundyou.service.RouteAnalyzer;
import mapsaroundyou.storage.DatasetMetadataRepository;
import mapsaroundyou.storage.DestinationRepository;
import mapsaroundyou.storage.ListingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DefaultSearchLogic implements SearchLogic {
    private final DestinationRepository destinationRepository;
    private final ListingRepository listingRepository;
    private final DatasetMetadataRepository datasetMetadataRepository;
    private final ListingFilter listingFilter;
    private final CommuteEstimator commuteEstimator;
    private final ListingRanker listingRanker;
    private final RouteAnalyzer routeAnalyzer;

    private UserPreferences currentPreferences;

    public DefaultSearchLogic(
            DestinationRepository destinationRepository,
            ListingRepository listingRepository,
            DatasetMetadataRepository datasetMetadataRepository,
            ListingFilter listingFilter,
            CommuteEstimator commuteEstimator,
            ListingRanker listingRanker,
            RouteAnalyzer routeAnalyzer
    ) {
        this.destinationRepository = destinationRepository;
        this.listingRepository = listingRepository;
        this.datasetMetadataRepository = datasetMetadataRepository;
        this.listingFilter = listingFilter;
        this.commuteEstimator = commuteEstimator;
        this.listingRanker = listingRanker;
        this.routeAnalyzer = routeAnalyzer;
        this.currentPreferences = AppConfig.defaultUserPreferences();
    }

    @Override
    public List<Destination> getSupportedDestinations() {
        return destinationRepository.findAll();
    }

    @Override
    public DatasetMetadata getDatasetMetadata() {
        return datasetMetadataRepository.load();
    }

    @Override
    public void updatePreferences(UserPreferences preferences) {
        if (preferences == null) {
            throw new InvalidInputException("Preferences must not be null.");
        }
        if (preferences.maxRent() < 0) {
            throw new InvalidInputException("Maximum rent must be at least 0.");
        }
        if (preferences.maxCommuteMinutes() < 1) {
            throw new InvalidInputException("Maximum commute must be at least 1 minute.");
        }
        if (preferences.maxWalkMinutes() < 0) {
            throw new InvalidInputException("Maximum walk must be at least 0 minutes.");
        }
        if (preferences.transportMode() == null) {
            throw new InvalidInputException("Transport mode must not be null.");
        }
        if (preferences.resultLimit() < 1) {
            throw new InvalidInputException("Result limit must be at least 1.");
        }
        if (preferences.sortMode() == null) {
            throw new InvalidInputException("Sort mode must not be null.");
        }

        String normalizedDestinationId = normalizeDestinationId(preferences.destinationId());
        if (normalizedDestinationId != null) {
            ensureDestinationExists(normalizedDestinationId);
        }

        currentPreferences = new UserPreferences(
                normalizedDestinationId,
                preferences.maxRent(),
                preferences.maxCommuteMinutes(),
                preferences.maxWalkMinutes(),
                preferences.requireAircon(),
                preferences.transportMode(),
                preferences.resultLimit(),
                preferences.sortMode(),
                preferences.excludeWalkDominantRoutes()
        );
    }

    @Override
    public List<SearchResult> generateShortlist() {
        validateSearchReady();
        List<RentalListing> filteredListings = listingFilter.filterByRent(
                listingRepository.findAll(),
                currentPreferences.maxRent()
        );
        filteredListings = listingFilter.filterByAircon(filteredListings, currentPreferences.requireAircon());

        List<SearchResult> results = new ArrayList<>();
        for (RentalListing listing : filteredListings) {
            CommuteEstimate commute = commuteEstimator.estimate(
                    listing.originNodeId(),
                    currentPreferences.destinationId(),
                    currentPreferences.transportMode()
            );
            if (commute.totalMinutes() > currentPreferences.maxCommuteMinutes()) {
                continue;
            }
            if (commute.walkMinutes() > currentPreferences.maxWalkMinutes()) {
                continue;
            }
            if (currentPreferences.excludeWalkDominantRoutes() && routeAnalyzer.isWalkDominant(commute)) {
                continue;
            }

            SearchResult interimResult = new SearchResult(listing, commute, 0.0d);
            double score = listingRanker.computeScore(
                    interimResult,
                    Math.max(1, currentPreferences.maxRent()),
                    Math.max(1, currentPreferences.maxCommuteMinutes())
            );
            results.add(new SearchResult(listing, commute, score));
        }

        List<SearchResult> rankedResults = listingRanker.rank(results, currentPreferences.sortMode()).stream()
                .limit(currentPreferences.resultLimit())
                .toList();
        if (rankedResults.isEmpty()) {
            throw new NoResultsException("No listings match your filters. Try relaxing rent or commute limits.");
        }
        return rankedResults;
    }

    @Override
    public ListingDetails getListingDetails(String listingId) {
        RentalListing listing = getListing(listingId);
        Optional<CommuteEstimate> commuteEstimate = Optional.empty();
        if (currentPreferences.destinationId() != null && !currentPreferences.destinationId().isBlank()) {
            commuteEstimate = Optional.of(commuteEstimator.estimate(
                    listing.originNodeId(),
                    currentPreferences.destinationId(),
                    currentPreferences.transportMode()
            ));
        }
        return new ListingDetails(listing, commuteEstimate);
    }

    @Override
    public CommuteEstimate getCommuteDetails(String listingId) {
        validateSearchReady();
        RentalListing listing = getListing(listingId);
        return commuteEstimator.estimate(
                listing.originNodeId(),
                currentPreferences.destinationId(),
                currentPreferences.transportMode()
        );
    }

    @Override
    public UserPreferences getCurrentPreferences() {
        return currentPreferences;
    }

    private void validateSearchReady() {
        if (currentPreferences.destinationId() == null || currentPreferences.destinationId().isBlank()) {
            throw new InvalidInputException("Destination must be set before searching.");
        }
        ensureDestinationExists(currentPreferences.destinationId());
        if (currentPreferences.maxCommuteMinutes() < 1) {
            throw new InvalidInputException("Search preferences have not been set.");
        }
    }

    private void ensureDestinationExists(String destinationId) {
        destinationRepository.findById(destinationId)
                .orElseThrow(() -> new DestinationNotFoundException(
                        "Unknown destination. Please select a supported destination id."));
    }

    private static String normalizeDestinationId(String destinationId) {
        if (destinationId == null || destinationId.isBlank()) {
            return null;
        }
        return destinationId.trim();
    }

    private RentalListing getListing(String listingId) {
        if (listingId == null || listingId.isBlank()) {
            throw new InvalidInputException("Listing id must not be blank.");
        }
        return listingRepository.findById(listingId.trim())
                .orElseThrow(() -> new ListingNotFoundException(
                        "Listing not found. It may have been removed from the dataset."));
    }
}
