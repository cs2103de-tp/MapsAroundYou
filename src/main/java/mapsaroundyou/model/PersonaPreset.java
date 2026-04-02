package mapsaroundyou.model;

import java.util.Optional;

/**
 * Persona presets intended for first-time UX onboarding.
 *
 * <p>Presets only provide deterministic defaults for GUI input fields; users can always
 * manually override the populated values afterward.</p>
 */
public enum PersonaPreset {
    NEW_USER(
            "New User",
            null
    ),
    STUDENT(
            "Student",
            new PersonaPresetAppliedValues(
                    1400,
                    50,
                    false
            )
    ),
    WORKER(
            "Worker",
            new PersonaPresetAppliedValues(
                    2000,
                    65,
                    false
            )
    ),
    CUSTOM("Custom", null);

    private final String displayName;
    private final PersonaPresetAppliedValues defaultValues;

    PersonaPreset(String displayName, PersonaPresetAppliedValues defaultValues) {
        this.displayName = displayName;
        this.defaultValues = defaultValues;
    }

    public String displayName() {
        return displayName;
    }

    public Optional<PersonaPresetAppliedValues> defaultValues() {
        return Optional.ofNullable(defaultValues);
    }

    @Override
    public String toString() {
        return displayName;
    }
}

