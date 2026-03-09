package mapsaroundyou.cli;

record ParsedCommand(CommandType commandType, SearchCommandArguments searchArguments) {
    enum CommandType {
        HELP,
        INTERACTIVE,
        SEARCH
    }
}
