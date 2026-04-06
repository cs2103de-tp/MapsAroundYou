package mapsaroundyou.cli;

import mapsaroundyou.common.InvalidInputException;
import mapsaroundyou.model.SortMode;

import java.util.HashMap;
import java.util.Map;

/**
 * Small parser for the scaffold CLI surface.
 */
public class CliCommandParser {
    private static final java.util.Set<String> VALUE_FLAGS = java.util.Set.of(
            "--destination",
            "--max-rent",
            "--max-commute",
            "--max-walk",
            "--result-limit",
            "--sort"
    );

    public ParsedCommand parse(String[] args) {
        if (args.length == 0) {
            return new ParsedCommand(ParsedCommand.CommandType.INTERACTIVE, null);
        }

        String firstArg = args[0];
        if (isHelp(firstArg)) {
            return new ParsedCommand(ParsedCommand.CommandType.HELP, null);
        }

        if (!"search".equals(firstArg)) {
            throw new InvalidInputException("Unknown command: " + firstArg);
        }

        if (args.length == 2 && isHelp(args[1])) {
            return new ParsedCommand(ParsedCommand.CommandType.HELP, null);
        }

        return new ParsedCommand(ParsedCommand.CommandType.SEARCH, parseSearchArguments(args));
    }

    private SearchCommandArguments parseSearchArguments(String[] args) {
        Map<String, String> options = new HashMap<>();
        boolean requireAircon = false;
        boolean excludeWalkDominantRoutes = false;

        for (int index = 1; index < args.length; index++) {
            String token = args[index];
            if ("--require-aircon".equals(token)) {
                requireAircon = true;
                continue;
            }
            if ("--exclude-walk-dominant".equals(token)) {
                excludeWalkDominantRoutes = true;
                continue;
            }
            if (!token.startsWith("--")) {
                throw new InvalidInputException("Expected a named flag but got: " + token);
            }
            if (!VALUE_FLAGS.contains(token)) {
                throw new InvalidInputException("Unknown flag: " + token);
            }
            if (index + 1 >= args.length) {
                throw new InvalidInputException("Missing value for flag: " + token);
            }
            options.put(token, args[++index]);
        }

        String destinationId = requireOption(options, "--destination");
        int maxRent = parsePositiveOrZeroInt(requireOption(options, "--max-rent"), "--max-rent");
        int maxCommute = parsePositiveInt(requireOption(options, "--max-commute"), "--max-commute");
        Integer maxWalkMinutes = parseOptionalPositiveOrZeroInt(options.get("--max-walk"), "--max-walk");
        Integer resultLimit = parseOptionalPositiveInt(options.get("--result-limit"), "--result-limit");
        SortMode sortMode = parseOptionalSortMode(options.get("--sort"));
        return new SearchCommandArguments(
                destinationId,
                maxRent,
                maxCommute,
                maxWalkMinutes,
                requireAircon,
                resultLimit,
                sortMode,
                excludeWalkDominantRoutes
        );
    }

    private static String requireOption(Map<String, String> options, String optionName) {
        String value = options.get(optionName);
        if (value == null || value.isBlank()) {
            throw new InvalidInputException("Missing required flag: " + optionName);
        }
        return value.trim();
    }

    private static int parsePositiveInt(String rawValue, String optionName) {
        int value = parseInteger(rawValue, optionName);
        if (value < 1) {
            throw new InvalidInputException(optionName + " must be at least 1.");
        }
        return value;
    }

    private static int parsePositiveOrZeroInt(String rawValue, String optionName) {
        int value = parseInteger(rawValue, optionName);
        if (value < 0) {
            throw new InvalidInputException(optionName + " must be at least 0.");
        }
        return value;
    }

    private static Integer parseOptionalPositiveInt(String rawValue, String optionName) {
        if (rawValue == null) {
            return null;
        }
        return parsePositiveInt(rawValue, optionName);
    }

    private static Integer parseOptionalPositiveOrZeroInt(String rawValue, String optionName) {
        if (rawValue == null) {
            return null;
        }
        return parsePositiveOrZeroInt(rawValue, optionName);
    }

    private static SortMode parseOptionalSortMode(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }
        try {
            return SortMode.fromCliValue(rawValue);
        } catch (IllegalArgumentException exception) {
            throw new InvalidInputException(exception.getMessage());
        }
    }

    private static int parseInteger(String rawValue, String optionName) {
        try {
            return Integer.parseInt(rawValue);
        } catch (NumberFormatException exception) {
            throw new InvalidInputException(optionName + " must be a valid integer.");
        }
    }

    private static boolean isHelp(String token) {
        return "-h".equals(token) || "--help".equals(token) || "help".equals(token);
    }
}
