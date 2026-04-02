package mapsaroundyou.gui;

import mapsaroundyou.model.PersonaPreset;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.prefs.Preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UiSettingsStoreTest {
    @Test
    void loadPersonaPreset_whenMissing_defaultsToNewUser() throws Exception {
        Preferences prefs = testNode();
        try {
            UiSettingsStore store = new UiSettingsStore(prefs);

            assertEquals(PersonaPreset.NEW_USER, store.loadPersonaPreset());
            assertFalse(store.isDarkModeEnabled());
        } finally {
            cleanup(prefs);
        }
    }

    @Test
    void savePersonaPreset_persistsAcrossStoreInstances() throws Exception {
        Preferences prefs = testNode();
        try {
            UiSettingsStore store = new UiSettingsStore(prefs);
            store.savePersonaPreset(PersonaPreset.STUDENT);
            prefs.flush();

            UiSettingsStore reloaded = new UiSettingsStore(prefs);
            assertEquals(PersonaPreset.STUDENT, reloaded.loadPersonaPreset());
        } finally {
            cleanup(prefs);
        }
    }

    @Test
    void darkModeEnabled_persistsAcrossStoreInstances() throws Exception {
        Preferences prefs = testNode();
        try {
            UiSettingsStore store = new UiSettingsStore(prefs);
            store.setDarkModeEnabled(true);
            prefs.flush();

            UiSettingsStore reloaded = new UiSettingsStore(prefs);
            assertTrue(reloaded.isDarkModeEnabled());
        } finally {
            cleanup(prefs);
        }
    }

    private static Preferences testNode() {
        String nodeName = "mapsaroundyou-ui-settings-test-" + UUID.randomUUID();
        return Preferences.userRoot().node(nodeName);
    }

    private static void cleanup(Preferences prefs) {
        try {
            prefs.removeNode();
        } catch (IllegalStateException | java.util.prefs.BackingStoreException ignored) {
            // Best-effort cleanup; the test correctness doesn't depend on persistence removal.
        }
    }
}

