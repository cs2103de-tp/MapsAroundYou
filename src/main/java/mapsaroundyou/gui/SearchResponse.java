package mapsaroundyou.gui;

import mapsaroundyou.model.DatasetMetadata;
import mapsaroundyou.model.SearchResult;

import java.util.List;

public record SearchResponse(
        DatasetMetadata datasetMetadata,
        List<SearchResult> results
) {
    public SearchResponse {
        results = List.copyOf(results);
    }
}

