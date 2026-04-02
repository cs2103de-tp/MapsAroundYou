package mapsaroundyou.app;

import mapsaroundyou.logic.SearchLogic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationFactoryTest {
    @Test
    void createSearchLogic_returnsInitializedSearchLogic() {
        SearchLogic searchLogic = ApplicationFactory.createSearchLogic();

        assertNotNull(searchLogic);
        assertTrue(searchLogic.getCurrentPreferences() != null);
        assertFalse(searchLogic.getSupportedDestinations().isEmpty());
        assertNotNull(searchLogic.getDatasetMetadata());
    }
}
