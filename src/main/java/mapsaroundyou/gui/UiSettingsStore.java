package mapsaroundyou.gui;

import mapsaroundyou.model.PersonaPreset;

import java.util.Objects;
import java.util.prefs.Preferences;

/**
 * Persists a small amount of UI state (persona preset + dark mode) for onboarding/UX.
 *
 * <p>All values are treated as best-effort; missing/invalid values fall back to
 * deterministic defaults.</p>
 */
public final class UiSettingsStore {
    private static final String KEY_PERSONA_PRESET = "personaPreset";
    private static final String KEY_DARK_MODE_ENABLED = "darkModeEnabled";

    private final Preferences preferences;

    public UiSettingsStore() {
        this(Preferences.userNodeForPackage(UiSettingsStore.class));
    }

    UiSettingsStore(Preferences preferences) {
        this.preferences = Objects.requireNonNull(preferences, "preferences");
    }

    public PersonaPreset loadPersonaPreset() {
        String stored = preferences.get(KEY_PERSONA_PRESET, null);
        if (stored == null) {
            return PersonaPreset.NEW_USER;
        }

        try {
            return PersonaPreset.valueOf(stored);
        } catch (IllegalArgumentException exception) {
            return PersonaPreset.NEW_USER;
        }
    }

    public void savePersonaPreset(PersonaPreset preset) {
        Objects.requireNonNull(preset, "preset");
        preferences.put(KEY_PERSONA_PRESET, preset.name());
    }

    public boolean isDarkModeEnabled() {
        return preferences.getBoolean(KEY_DARK_MODE_ENABLED, false);
    }

    public void setDarkModeEnabled(boolean enabled) {
        preferences.putBoolean(KEY_DARK_MODE_ENABLED, enabled);
    }
}

