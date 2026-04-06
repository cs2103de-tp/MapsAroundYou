package mapsaroundyou.logic;

import mapsaroundyou.common.InvalidInputException;
import mapsaroundyou.model.CommuteEstimate;
import mapsaroundyou.model.DatasetMetadata;
import mapsaroundyou.model.Destination;
import mapsaroundyou.model.RentalListing;
import mapsaroundyou.model.SearchResult;
import mapsaroundyou.model.SortMode;
import mapsaroundyou.model.TransportMode;
import mapsaroundyou.model.UserPreferences;
import mapsaroundyou.service.CommuteEstimator;
import mapsaroundyou.service.ListingFilter;
import mapsaroundyou.service.ListingRanker;
import mapsaroundyou.service.RouteAnalyzer;
import mapsaroundyou.storage.DatasetMetadataRepository;
import mapsaroundyou.storage.DestinationRepository;
import mapsaroundyou.storage.ListingRepository;
import mapsaroundyou.storage.TravelTimeRepository;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultSearchLogicTest {
    @Test
    void generateShortlist_appliesCommuteCapAndDeterministicRanking() {
        DefaultSearchLogic logic = createLogic(
                List.of(
                        new RentalListing("L001", "Listing A", 1500, true, "R01", "Addr 1", "HDB", "PG", "Note"),
                        new RentalListing("L002", "Listing B", 1300, true, "R02", "Addr 2", "HDB", "PG", "Note"),
                        new RentalListing("L003", "Listing C", 1250, false, "R03", "Addr 3", "HDB", "PG", "Note")
                ),
                Map.of(
                        "R01:D01", new CommuteEstimate("R01", "D01", 30, 20, 10, 0, 1.50d),
                        "R02:D01", new CommuteEstimate("R02", "D01", 30, 24, 6, 0, 1.60d),
                        "R03:D01", new CommuteEstimate("R03", "D01", 50, 40, 10, 0, 1.80d)
                )
        );

        logic.updatePreferences(new UserPreferences(
                "D01",
                1600,
                45,
                10,
                true,
                TransportMode.PUBLIC_TRANSPORT,
                10,
                SortMode.COMMUTE,
                false
        ));

        List<SearchResult> results = logic.generateShortlist();

        assertEquals(List.of("L002", "L001"),
                results.stream().map(result -> result.listing().listingId()).toList());
    }

    @Test
    void generateShortlist_excludesListingsAboveMaxWalkMinutes() {
        DefaultSearchLogic logic = createLogic(
                List.of(
                        new RentalListing("L001", "Listing A", 1500, true, "R01", "Addr 1", "HDB", "PG", "Note"),
                        new RentalListing("L002", "Listing B", 1400, true, "R02", "Addr 2", "HDB", "PG", "Note")
                ),
                Map.of(
                        "R01:D01", new CommuteEstimate("R01", "D01", 30, 19, 11, 0, 1.50d),
                        "R02:D01", new CommuteEstimate("R02", "D01", 30, 20, 10, 0, 1.60d)
                )
        );

        logic.updatePreferences(new UserPreferences(
                "D01",
                2000,
                45,
                10,
                false,
                TransportMode.PUBLIC_TRANSPORT,
                10,
                SortMode.COMMUTE,
                false
        ));

        List<SearchResult> results = logic.generateShortlist();

        assertEquals(List.of("L002"),
                results.stream().map(result -> result.listing().listingId()).toList());
    }

    @Test
    void generateShortlist_excludesWalkDominantRoutesOnlyWhenEnabled() {
        DefaultSearchLogic logic = createLogic(
                List.of(
                        new RentalListing("L001", "Listing A", 1500, true, "R01", "Addr 1", "HDB", "PG", "Note"),
                        new RentalListing("L002", "Listing B", 1400, true, "R02", "Addr 2", "HDB", "PG", "Note")
                ),
                Map.of(
                        "R01:D01", new CommuteEstimate("R01", "D01", 30, 12, 18, 0, 1.50d),
                        "R02:D01", new CommuteEstimate("R02", "D01", 30, 24, 6, 0, 1.60d)
                )
        );

        logic.updatePreferences(new UserPreferences(
                "D01",
                2000,
                45,
                20,
                false,
                TransportMode.PUBLIC_TRANSPORT,
                10,
                SortMode.COMMUTE,
                false
        ));
        assertEquals(List.of("L002", "L001"),
                logic.generateShortlist().stream().map(result -> result.listing().listingId()).toList());

        logic.updatePreferences(new UserPreferences(
                "D01",
                2000,
                45,
                20,
                false,
                TransportMode.PUBLIC_TRANSPORT,
                10,
                SortMode.COMMUTE,
                true
        ));
        assertEquals(List.of("L002"),
                logic.generateShortlist().stream().map(result -> result.listing().listingId()).toList());
    }

    @Test
    void generateShortlist_appliesBalancedSortAndResultLimit() {
        DefaultSearchLogic logic = createLogic(
                List.of(
                        new RentalListing("L001", "Listing A", 2400, true, "R01", "Addr 1", "HDB", "PG", "Note"),
                        new RentalListing("L002", "Listing B", 1200, true, "R02", "Addr 2", "HDB", "PG", "Note"),
                        new RentalListing("L003", "Listing C", 1500, true, "R03", "Addr 3", "HDB", "PG", "Note")
                ),
                Map.of(
                        "R01:D01", new CommuteEstimate("R01", "D01", 20, 16, 4, 0, 1.50d),
                        "R02:D01", new CommuteEstimate("R02", "D01", 30, 22, 8, 0, 1.60d),
                        "R03:D01", new CommuteEstimate("R03", "D01", 40, 35, 5, 0, 1.70d)
                )
        );

        logic.updatePreferences(new UserPreferences(
                "D01",
                3000,
                60,
                10,
                false,
                TransportMode.PUBLIC_TRANSPORT,
                2,
                SortMode.BALANCED,
                false
        ));

        List<SearchResult> results = logic.generateShortlist();

        assertEquals(List.of("L002", "L001"),
                results.stream().map(result -> result.listing().listingId()).toList());
    }

    @Test
    void updatePreferences_rejectsInvalidResultLimit() {
        DefaultSearchLogic logic = createLogic(List.of(), Map.of());

        InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> logic.updatePreferences(new UserPreferences(
                        "D01",
                        2000,
                        45,
                        10,
                        false,
                        TransportMode.PUBLIC_TRANSPORT,
                        0,
                        SortMode.COMMUTE,
                        false
                ))
        );

        assertEquals("Result limit must be at least 1.", exception.getMessage());
    }

    private static DefaultSearchLogic createLogic(
            List<RentalListing> listings,
            Map<String, CommuteEstimate> commuteByPair
    ) {
        DestinationRepository destinationRepository = new InMemoryDestinationRepository(List.of(
                new Destination("D01", "NUS", "University", "", "117575")
        ));
        ListingRepository listingRepository = new InMemoryListingRepository(listings);
        TravelTimeRepository travelTimeRepository = new InMemoryTravelTimeRepository(commuteByPair);
        DatasetMetadataRepository datasetMetadataRepository =
                () -> new DatasetMetadata(LocalDate.of(2026, 3, 8), "Fixture dataset");

        return new DefaultSearchLogic(
                destinationRepository,
                listingRepository,
                datasetMetadataRepository,
                new ListingFilter(),
                new CommuteEstimator(travelTimeRepository),
                new ListingRanker(),
                new RouteAnalyzer(0.6d)
        );
    }

    private static final class InMemoryDestinationRepository implements DestinationRepository {
        private final Map<String, Destination> destinationsById;

        private InMemoryDestinationRepository(List<Destination> destinations) {
            this.destinationsById = new LinkedHashMap<>();
            for (Destination destination : destinations) {
                destinationsById.put(destination.destinationId(), destination);
            }
        }

        @Override
        public List<Destination> findAll() {
            return List.copyOf(destinationsById.values());
        }

        @Override
        public Optional<Destination> findById(String destinationId) {
            return Optional.ofNullable(destinationsById.get(destinationId));
        }
    }

    private static final class InMemoryListingRepository implements ListingRepository {
        private final Map<String, RentalListing> listingsById;

        private InMemoryListingRepository(List<RentalListing> listings) {
            this.listingsById = new LinkedHashMap<>();
            for (RentalListing listing : listings) {
                listingsById.put(listing.listingId(), listing);
            }
        }

        @Override
        public List<RentalListing> findAll() {
            return List.copyOf(listingsById.values());
        }

        @Override
        public Optional<RentalListing> findById(String listingId) {
            return Optional.ofNullable(listingsById.get(listingId));
        }
    }

    private static final class InMemoryTravelTimeRepository implements TravelTimeRepository {
        private final Map<String, CommuteEstimate> commuteByPair;

        private InMemoryTravelTimeRepository(Map<String, CommuteEstimate> commuteByPair) {
            this.commuteByPair = commuteByPair;
        }

        @Override
        public Optional<CommuteEstimate> findByOriginAndDestination(String originNodeId, String destinationId) {
            return Optional.ofNullable(commuteByPair.get(originNodeId + ":" + destinationId));
        }

        @Override
        public Set<String> findKnownOrigins() {
            return commuteByPair.values().stream()
                    .map(CommuteEstimate::originNodeId)
                    .collect(java.util.stream.Collectors.toUnmodifiableSet());
        }

        @Override
        public Set<String> findKnownDestinations() {
            return commuteByPair.values().stream()
                    .map(CommuteEstimate::destinationId)
                    .collect(java.util.stream.Collectors.toUnmodifiableSet());
        }

        @Override
        public Map<String, Set<String>> findKnownDestinationsByOrigin() {
            Map<String, Set<String>> destinationsByOrigin = new LinkedHashMap<>();
            for (CommuteEstimate commuteEstimate : commuteByPair.values()) {
                destinationsByOrigin.computeIfAbsent(
                                commuteEstimate.originNodeId(),
                                ignored -> new java.util.LinkedHashSet<>())
                        .add(commuteEstimate.destinationId());
            }
            return destinationsByOrigin.entrySet().stream()
                    .collect(java.util.stream.Collectors.toUnmodifiableMap(
                            Map.Entry::getKey,
                            entry -> Set.copyOf(entry.getValue())
                    ));
        }
    }
}
