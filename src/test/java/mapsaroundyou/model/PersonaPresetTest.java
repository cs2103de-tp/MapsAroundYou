package mapsaroundyou.model;

import mapsaroundyou.common.InvalidInputException;
import mapsaroundyou.gui.GuiSearchRequestParser;
import mapsaroundyou.gui.SearchRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonaPresetTest {
    private static final Destination DESTINATION = new Destination(
            "D01",
            "NUS",
            "University",
            "Kent Ridge",
            "117575"
    );

    @Test
    void studentPreset_defaultValues_areDeterministic() {
        PersonaPresetAppliedValues values = PersonaPreset.STUDENT.defaultValues().orElseThrow();

        assertEquals(1400, values.maxRent());
        assertEquals(50, values.maxCommuteMinutes());
        assertEquals(false, values.requireAircon());
    }

    @Test
    void workerPreset_defaultValues_areDeterministic() {
        PersonaPresetAppliedValues values = PersonaPreset.WORKER.defaultValues().orElseThrow();

        assertEquals(2000, values.maxRent());
        assertEquals(65, values.maxCommuteMinutes());
        assertEquals(false, values.requireAircon());
    }

    @Test
    void customPreset_hasNoDefaults() {
        assertTrue(PersonaPreset.CUSTOM.defaultValues().isEmpty());
    }

    @Test
    void newUserPreset_hasNoDefaults() {
        assertTrue(PersonaPreset.NEW_USER.defaultValues().isEmpty());
    }

    @Test
    void presetAppliedValuesAreParseableAndManualEditsAreUsed() {
        PersonaPresetAppliedValues studentValues = PersonaPreset.STUDENT.defaultValues().orElseThrow();

        SearchRequest parsedUsingDefaults = GuiSearchRequestParser.parse(
                DESTINATION,
                String.valueOf(studentValues.maxRent()),
                String.valueOf(studentValues.maxCommuteMinutes()),
                studentValues.requireAircon()
        );
        assertEquals(studentValues.maxRent(), parsedUsingDefaults.maxRent());
        assertEquals(studentValues.maxCommuteMinutes(), parsedUsingDefaults.maxCommuteMinutes());

        // Simulate a user overriding the commute cap after selecting the preset.
        int overriddenCommuteCap = 60;
        SearchRequest parsedUsingOverride = GuiSearchRequestParser.parse(
                DESTINATION,
                String.valueOf(studentValues.maxRent()),
                String.valueOf(overriddenCommuteCap),
                studentValues.requireAircon()
        );
        assertEquals(overriddenCommuteCap, parsedUsingOverride.maxCommuteMinutes());
    }

    @Test
    void invalidManualEditAfterPreset_throwsValidationError() {
        PersonaPresetAppliedValues studentValues = PersonaPreset.STUDENT.defaultValues().orElseThrow();

        InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> GuiSearchRequestParser.parse(
                        DESTINATION,
                        "-1",
                        String.valueOf(studentValues.maxCommuteMinutes()),
                        studentValues.requireAircon()
                )
        );

        assertEquals("Max rent must be at least 0.", exception.getMessage());
    }
}

