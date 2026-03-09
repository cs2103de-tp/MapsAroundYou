package mapsaroundyou.cli;

import mapsaroundyou.common.AppConfig;
import mapsaroundyou.common.DataLoadException;
import mapsaroundyou.logic.DefaultSearchLogic;
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
import mapsaroundyou.storage.PropertiesDatasetMetadataRepository;

public final class MapsAroundYouApp {
    private MapsAroundYouApp() {
    }

    public static void main(String[] args) {
        int exitCode;
        try {
            CliApplication cliApplication = buildApplication();
            exitCode = cliApplication.run(args);
        } catch (DataLoadException exception) {
            System.err.println("Startup error: " + exception.getMessage());
            exitCode = 1;
        }
        System.exit(exitCode);
    }

    private static CliApplication buildApplication() {
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

        SearchLogic searchLogic = new DefaultSearchLogic(
                destinationRepository,
                listingRepository,
                datasetMetadataRepository,
                new ListingFilter(),
                new CommuteEstimator(travelTimeRepository),
                new ListingRanker(),
                new RouteAnalyzer(AppConfig.DEFAULT_WALK_DOMINANT_THRESHOLD)
        );
        return new CliApplication(searchLogic, new CliCommandParser(), new CliPrinter());
    }
}
