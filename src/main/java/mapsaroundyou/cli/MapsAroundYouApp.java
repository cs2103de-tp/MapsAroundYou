package mapsaroundyou.cli;

import mapsaroundyou.app.ApplicationFactory;
import mapsaroundyou.common.DataLoadException;
import mapsaroundyou.logic.SearchLogic;

public final class MapsAroundYouApp {
    private MapsAroundYouApp() {
    }

    public static void main(String[] args) {
        int exitCode;
        try {
            CliApplication cliApplication = buildApplication();
            exitCode = cliApplication.run(args);
        } catch (DataLoadException exception) {
            System.err.println("Startup error: " + exception.getMessage());
            exitCode = 1;
        }
        System.exit(exitCode);
    }

    private static CliApplication buildApplication() {
        SearchLogic searchLogic = ApplicationFactory.createSearchLogic();
        return new CliApplication(searchLogic, new CliCommandParser(), new CliPrinter());
    }
}
