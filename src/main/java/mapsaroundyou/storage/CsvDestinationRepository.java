package mapsaroundyou.storage;

import mapsaroundyou.common.DataLoadException;
import mapsaroundyou.model.Destination;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class CsvDestinationRepository implements DestinationRepository {
    private static final String[] REQUIRED_HEADERS = {"ID", "Category", "Location Name", "Postal Code"};

    private final Map<String, Destination> destinationsById;

    public CsvDestinationRepository(String resourcePath) {
        this(CsvSupport.classpathReader(resourcePath), resourcePath);
    }

    public CsvDestinationRepository(Path filePath) {
        this(CsvSupport.fileReader(filePath), filePath.toString());
    }

    private CsvDestinationRepository(ReaderSupplier readerSupplier, String sourceName) {
        this.destinationsById = load(readerSupplier, sourceName);
    }

    @Override
    public List<Destination> findAll() {
        return List.copyOf(destinationsById.values());
    }

    @Override
    public Optional<Destination> findById(String destinationId) {
        return Optional.ofNullable(destinationsById.get(destinationId));
    }

    private static Map<String, Destination> load(ReaderSupplier readerSupplier, String sourceName) {
        Map<String, Destination> destinations = new LinkedHashMap<>();
        try (CSVParser parser = CsvSupport.openParser(readerSupplier, sourceName, REQUIRED_HEADERS)) {
            for (CSVRecord record : parser) {
                String destinationId = CsvSupport.requireValue(record, "ID", sourceName);
                if (destinations.containsKey(destinationId)) {
                    throw new DataLoadException("Duplicate destination id in " + sourceName + ": " + destinationId);
                }

                Destination destination = new Destination(
                        destinationId,
                        CsvSupport.requireValue(record, "Location Name", sourceName),
                        CsvSupport.requireValue(record, "Category", sourceName),
                        "",
                        CsvSupport.requireValue(record, "Postal Code", sourceName)
                );
                destinations.put(destinationId, destination);
            }
        } catch (java.io.IOException exception) {
            throw new DataLoadException("Failed to close dataset: " + sourceName, exception);
        }
        if (destinations.isEmpty()) {
            throw new DataLoadException("No destinations found in " + sourceName);
        }
        return destinations;
    }
}
