package mapsaroundyou.gui;

import javafx.application.Application;

/**
 * Non-Application entry point so the shadow JAR can bootstrap JavaFX
 * without module-path issues.
 */
public final class Launcher {
    private Launcher() {
    }

    public static void main(String[] args) {
        Application.launch(MapsAroundYouGuiApp.class, args);
    }
}
