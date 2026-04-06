package mapsaroundyou.storage;

import mapsaroundyou.model.UserPreferences;

/**
 * Persists last-used search preferences outside the bundled app data.
 */
public interface UserPrefsRepository {
    UserPreferences load();

    void save(UserPreferences preferences);
}
