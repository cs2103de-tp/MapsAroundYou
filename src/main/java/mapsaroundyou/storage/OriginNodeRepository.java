package mapsaroundyou.storage;

import mapsaroundyou.model.OriginNode;

import java.util.List;
import java.util.Optional;

public interface OriginNodeRepository {
    List<OriginNode> findAll();

    Optional<OriginNode> findById(String originNodeId);
}
