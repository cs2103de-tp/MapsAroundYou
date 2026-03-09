package mapsaroundyou.storage;

import mapsaroundyou.model.Destination;

import java.util.List;
import java.util.Optional;

public interface DestinationRepository {
    List<Destination> findAll();

    Optional<Destination> findById(String destinationId);
}
