package mapsaroundyou.gui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LauncherTest {

    @Test
    void windowsAarch64IsUnsupported() {
        assertTrue(Launcher.isUnsupportedWindowsArm("Windows 11", "aarch64"));
    }

    @Test
    void windowsArm64IsUnsupported() {
        assertTrue(Launcher.isUnsupportedWindowsArm("Windows 10", "arm64"));
    }

    @Test
    void windowsX64IsSupported() {
        assertFalse(Launcher.isUnsupportedWindowsArm("Windows 11", "amd64"));
    }

    @Test
    void linuxAarch64IsSupported() {
        assertFalse(Launcher.isUnsupportedWindowsArm("Linux", "aarch64"));
    }

    @Test
    void macOsAarch64IsSupported() {
        assertFalse(Launcher.isUnsupportedWindowsArm("Mac OS X", "aarch64"));
    }

    @Test
    void darwinDoesNotFalsePositive() {
        assertFalse(Launcher.isUnsupportedWindowsArm("Darwin", "aarch64"));
    }
}
