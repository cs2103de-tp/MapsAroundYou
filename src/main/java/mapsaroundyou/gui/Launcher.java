package mapsaroundyou.gui;

import javafx.application.Application;

/**
 * Non-Application entry point so the shadow JAR can bootstrap JavaFX
 * without module-path issues.
 */
public final class Launcher {
    private Launcher() {
    }

    static boolean isUnsupportedWindowsArm(String osName, String osArch) {
        String os = osName.toLowerCase();
        String arch = osArch.toLowerCase();
        return os.startsWith("windows")
                && (arch.equals("aarch64") || arch.equals("arm64"));
    }

    public static void main(String[] args) {
        String osName = System.getProperty("os.name", "");
        String osArch = System.getProperty("os.arch", "");
        if (isUnsupportedWindowsArm(osName, osArch)) {
            System.err.println(
                "ERROR: JavaFX does not support Windows ARM64 natively.\n"
                + "Please run this JAR with an x64 JDK instead.\n\n"
                + "Example:\n"
                + "  \"C:\\Program Files\\Microsoft\\jdk-21...-x64\\bin\\java.exe\""
                + " -jar <MapsAroundYou-...-all.jar>\n\n"
                + "Install an x64 JDK from: https://adoptium.net/");
            System.exit(1);
        }
        Application.launch(MapsAroundYouGuiApp.class, args);
    }
}
