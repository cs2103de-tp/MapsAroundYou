package mapsaroundyou.storage;

import mapsaroundyou.common.DataLoadException;
import mapsaroundyou.model.DatasetMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Properties;

public final class PropertiesDatasetMetadataRepository implements DatasetMetadataRepository {
    private final DatasetMetadata datasetMetadata;

    public PropertiesDatasetMetadataRepository(String resourcePath) {
        this.datasetMetadata = load(resourcePath);
    }

    @Override
    public DatasetMetadata load() {
        return datasetMetadata;
    }

    private static DatasetMetadata load(String resourcePath) {
        Properties properties = new Properties();
        try (InputStream inputStream = PropertiesDatasetMetadataRepository.class.getClassLoader()
                .getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new DataLoadException("Missing resource: " + resourcePath);
            }
            properties.load(inputStream);
        } catch (IOException exception) {
            throw new DataLoadException("Failed to read dataset metadata: " + resourcePath, exception);
        }

        String lastUpdated = properties.getProperty("lastUpdated");
        String sourceDescription = properties.getProperty("sourceDescription", "");
        if (lastUpdated == null || lastUpdated.isBlank()) {
            throw new DataLoadException("Missing lastUpdated in dataset metadata: " + resourcePath);
        }

        try {
            return new DatasetMetadata(LocalDate.parse(lastUpdated.trim()), sourceDescription.trim());
        } catch (RuntimeException exception) {
            throw new DataLoadException("Invalid lastUpdated value in dataset metadata: " + lastUpdated, exception);
        }
    }
}
