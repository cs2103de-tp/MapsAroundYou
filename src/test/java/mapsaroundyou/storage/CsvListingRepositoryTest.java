package mapsaroundyou.storage;

import mapsaroundyou.common.DataLoadException;
import mapsaroundyou.model.RentalListing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvListingRepositoryTest {
    @TempDir
    Path tempDir;

    @Test
    void findAll_validCsv_returnsListings() throws Exception {
        Path csvPath = tempDir.resolve("listings.csv");
        Files.writeString(csvPath, """
                listingId,title,monthlyRent,hasAircon,originNodeId,address,roomType,sourcePlatform,notes
                L001,Demo Listing,1500,true,R01,123 Demo Street,HDB room,PropertyGuru,Fixture
                """);

        CsvListingRepository repository = new CsvListingRepository(csvPath);

        List<RentalListing> listings = repository.findAll();
        assertEquals(1, listings.size());
        assertEquals("L001", listings.getFirst().listingId());
        assertEquals(1500, listings.getFirst().monthlyRent());
        assertEquals("R01", listings.getFirst().originNodeId());
    }

    @Test
    void constructor_missingColumn_throwsDataLoadException() throws Exception {
        Path csvPath = tempDir.resolve("broken-listings.csv");
        Files.writeString(csvPath, """
                listingId,title,monthlyRent,originNodeId,address,roomType,sourcePlatform,notes
                L001,Demo Listing,1500,R01,123 Demo Street,HDB room,PropertyGuru,Fixture
                """);

        DataLoadException exception = assertThrows(
                DataLoadException.class,
                () -> new CsvListingRepository(csvPath)
        );

        assertTrue(exception.getMessage().contains("hasAircon"));
    }
}
