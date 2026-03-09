package mapsaroundyou.storage;

import mapsaroundyou.model.DatasetMetadata;

public interface DatasetMetadataRepository {
    DatasetMetadata load();
}
