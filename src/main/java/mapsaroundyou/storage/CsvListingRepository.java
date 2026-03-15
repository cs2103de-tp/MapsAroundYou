package mapsaroundyou.storage;

import mapsaroundyou.common.DataLoadException;
import mapsaroundyou.model.RentalListing;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class CsvListingRepository implements ListingRepository {
    private static final String[] REQUIRED_HEADERS = {
            "listingId",
            "title",
            "monthlyRent",
            "hasAircon",
            "originNodeId",
            "address",
            "roomType",
            "sourcePlatform",
            "notes"
    };

    private final Map<String, RentalListing> listingsById;

    public CsvListingRepository(String resourcePath) {
        this(CsvSupport.classpathReader(resourcePath), resourcePath);
    }

    public CsvListingRepository(Path filePath) {
        this(CsvSupport.fileReader(filePath), filePath.toString());
    }

    private CsvListingRepository(ReaderSupplier readerSupplier, String sourceName) {
        this.listingsById = load(readerSupplier, sourceName);
    }

    @Override
    public List<RentalListing> findAll() {
        return List.copyOf(listingsById.values());
    }

    @Override
    public Optional<RentalListing> findById(String listingId) {
        return Optional.ofNullable(listingsById.get(listingId));
    }

    private static Map<String, RentalListing> load(ReaderSupplier readerSupplier, String sourceName) {
        Map<String, RentalListing> listings = new LinkedHashMap<>();
        try (CSVParser parser = CsvSupport.openParser(readerSupplier, sourceName, REQUIRED_HEADERS)) {
            for (CSVRecord record : parser) {
                String listingId = CsvSupport.requireValue(record, "listingId", sourceName);
                if (listings.containsKey(listingId)) {
                    throw new DataLoadException("Duplicate listing id in " + sourceName + ": " + listingId);
                }

                RentalListing listing = new RentalListing(
                        listingId,
                        CsvSupport.requireValue(record, "title", sourceName),
                        CsvSupport.parseRequiredInt(record, "monthlyRent", sourceName),
                        CsvSupport.parseRequiredBoolean(record, "hasAircon", sourceName),
                        CsvSupport.requireValue(record, "originNodeId", sourceName),
                        CsvSupport.requireValue(record, "address", sourceName),
                        CsvSupport.requireValue(record, "roomType", sourceName),
                        CsvSupport.requireValue(record, "sourcePlatform", sourceName),
                        CsvSupport.requireValue(record, "notes", sourceName)
                );
                listings.put(listingId, listing);
            }
        } catch (java.io.IOException exception) {
            throw new DataLoadException("Failed to close dataset: " + sourceName, exception);
        }
        if (listings.isEmpty()) {
            throw new DataLoadException("No listings found in " + sourceName);
        }
        return listings;
    }
}
