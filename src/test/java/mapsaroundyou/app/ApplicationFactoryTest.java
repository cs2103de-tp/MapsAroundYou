package mapsaroundyou.app;

import mapsaroundyou.logic.DefaultSearchLogic;
import mapsaroundyou.logic.SearchLogic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationFactoryTest {
    @Test
    void createSearchLogic_returnsInitializedDefaultSearchLogic() {
        SearchLogic searchLogic = ApplicationFactory.createSearchLogic();

        assertNotNull(searchLogic);
        assertTrue(searchLogic instanceof DefaultSearchLogic);
        assertFalse(searchLogic.getSupportedDestinations().isEmpty());
        assertNotNull(searchLogic.getDatasetMetadata());
    }
}
