package mapsaroundyou.storage;

import mapsaroundyou.common.DataLoadException;
import mapsaroundyou.model.OriginNode;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CsvOriginNodeRepository implements OriginNodeRepository {
    private static final String[] REQUIRED_HEADERS = {"Flat_ID", "Postal_Code", "Region", "Area_Name"};

    private final Map<String, OriginNode> originNodesById;

    public CsvOriginNodeRepository(String resourcePath) {
        this(CsvSupport.classpathReader(resourcePath), resourcePath);
    }

    public CsvOriginNodeRepository(Path filePath) {
        this(CsvSupport.fileReader(filePath), filePath.toString());
    }

    private CsvOriginNodeRepository(ReaderSupplier readerSupplier, String sourceName) {
        this.originNodesById = load(readerSupplier, sourceName);
    }

    @Override
    public List<OriginNode> findAll() {
        return List.copyOf(originNodesById.values());
    }

    @Override
    public Optional<OriginNode> findById(String originNodeId) {
        return Optional.ofNullable(originNodesById.get(originNodeId));
    }

    private static Map<String, OriginNode> load(ReaderSupplier readerSupplier, String sourceName) {
        Map<String, OriginNode> originNodes = new LinkedHashMap<>();
        try (CSVParser parser = CsvSupport.openParser(readerSupplier, sourceName, REQUIRED_HEADERS)) {
            for (CSVRecord record : parser) {
                String originNodeId = CsvSupport.requireValue(record, "Flat_ID", sourceName);
                if (originNodes.containsKey(originNodeId)) {
                    throw new DataLoadException("Duplicate origin node id in " + sourceName + ": " + originNodeId);
                }

                OriginNode originNode = new OriginNode(
                        originNodeId,
                        CsvSupport.requireValue(record, "Postal_Code", sourceName),
                        CsvSupport.requireValue(record, "Region", sourceName),
                        CsvSupport.requireValue(record, "Area_Name", sourceName)
                );
                originNodes.put(originNodeId, originNode);
            }
        } catch (java.io.IOException exception) {
            throw new DataLoadException("Failed to close dataset: " + sourceName, exception);
        }
        if (originNodes.isEmpty()) {
            throw new DataLoadException("No origin nodes found in " + sourceName);
        }
        return originNodes;
    }
}
