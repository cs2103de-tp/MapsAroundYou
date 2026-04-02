package mapsaroundyou.cli;

import mapsaroundyou.common.InvalidInputException;
import mapsaroundyou.model.SortMode;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CliCommandParserTest {
    @Test
    void parse_searchCommand_supportsExtendedFlags() {
        CliCommandParser parser = new CliCommandParser();

        ParsedCommand command = parser.parse(new String[]{
                "search",
                "--destination", "D01",
                "--max-rent", "2200",
                "--max-commute", "45",
                "--max-walk", "10",
                "--require-aircon",
                "--result-limit", "5",
                "--sort", "balanced",
                "--exclude-walk-dominant"
        });

        SearchCommandArguments arguments = command.searchArguments();
        assertEquals("D01", arguments.destinationId());
        assertEquals(2200, arguments.maxRent());
        assertEquals(45, arguments.maxCommuteMinutes());
        assertEquals(Integer.valueOf(10), arguments.maxWalkMinutes());
        assertEquals(true, arguments.requireAircon());
        assertEquals(Integer.valueOf(5), arguments.resultLimit());
        assertEquals(SortMode.BALANCED, arguments.sortMode());
        assertEquals(true, arguments.excludeWalkDominantRoutes());
    }

    @Test
    void parse_invalidSortValue_throwsInvalidInputException() {
        CliCommandParser parser = new CliCommandParser();

        InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> parser.parse(new String[]{
                        "search",
                        "--destination", "D01",
                        "--max-rent", "2200",
                        "--max-commute", "45",
                        "--sort", "fastest"
                })
        );

        assertEquals("Unknown sort mode: fastest", exception.getMessage());
    }
}
