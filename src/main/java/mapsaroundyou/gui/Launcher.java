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
        String arch = System.getProperty("os.arch", "");
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("win") && arch.equals("aarch64")) {
            System.err.println(
                "ERROR: JavaFX does not support Windows ARM64 natively.\n"
                + "Please run this JAR with an x64 JDK instead.\n\n"
                + "Example:\n"
                + "  \"C:\\Program Files\\Microsoft\\jdk-21...-x64\\bin\\java.exe\""
                + " -jar MapsAroundYou.jar\n\n"
                + "Install an x64 JDK from: https://adoptium.net/");
            System.exit(1);
        }
        Application.launch(MapsAroundYouGuiApp.class, args);
    }
}
