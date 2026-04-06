package mapsaroundyou.app;

import mapsaroundyou.common.AppConfig;
import mapsaroundyou.logic.DefaultSearchLogic;
import mapsaroundyou.logic.PersistentSearchLogic;
import mapsaroundyou.logic.SearchLogic;
import mapsaroundyou.service.CommuteEstimator;
import mapsaroundyou.service.ListingFilter;
import mapsaroundyou.service.ListingRanker;
import mapsaroundyou.service.RouteAnalyzer;
import mapsaroundyou.storage.AppDataValidator;
import mapsaroundyou.storage.CsvDestinationRepository;
import mapsaroundyou.storage.CsvListingRepository;
import mapsaroundyou.storage.CsvOriginNodeRepository;
import mapsaroundyou.storage.CsvTravelTimeRepository;
import mapsaroundyou.storage.PropertiesUserPrefsRepository;
import mapsaroundyou.storage.PropertiesDatasetMetadataRepository;

/**
 * Shared composition root for CLI and GUI.
 */
public final class ApplicationFactory {
    private ApplicationFactory() {
    }

    public static SearchLogic createSearchLogic() {
        CsvDestinationRepository destinationRepository =
                new CsvDestinationRepository(AppConfig.DESTINATIONS_RESOURCE);
        CsvOriginNodeRepository originNodeRepository =
                new CsvOriginNodeRepository(AppConfig.ORIGIN_NODES_RESOURCE);
        CsvListingRepository listingRepository =
                new CsvListingRepository(AppConfig.LISTINGS_RESOURCE);
        CsvTravelTimeRepository travelTimeRepository =
                new CsvTravelTimeRepository(AppConfig.TRAVEL_TIMES_RESOURCE);
        PropertiesDatasetMetadataRepository datasetMetadataRepository =
                new PropertiesDatasetMetadataRepository(AppConfig.DATASET_METADATA_RESOURCE);

        AppDataValidator.validate(
                originNodeRepository,
                destinationRepository,
                listingRepository,
                travelTimeRepository
        );

        return new PersistentSearchLogic(
                new DefaultSearchLogic(
                        destinationRepository,
                        listingRepository,
                        datasetMetadataRepository,
                        new ListingFilter(),
                        new CommuteEstimator(travelTimeRepository),
                        new ListingRanker(),
                        new RouteAnalyzer(AppConfig.DEFAULT_WALK_DOMINANT_THRESHOLD)
                ),
                new PropertiesUserPrefsRepository(AppConfig.userPreferencesPath())
        );
    }
}

