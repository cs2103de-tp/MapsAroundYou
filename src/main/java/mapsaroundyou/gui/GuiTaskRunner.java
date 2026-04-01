package mapsaroundyou.gui;

import javafx.concurrent.Task;

import java.util.Objects;

/**
 * Starts JavaFX background tasks on daemon threads with a consistent pattern.
 */
public final class GuiTaskRunner {
    private GuiTaskRunner() {
    }

    public static void run(Task<?> task, String threadName) {
        Objects.requireNonNull(task, "task");
        Objects.requireNonNull(threadName, "threadName");

        Thread thread = new Thread(task, threadName);
        thread.setDaemon(true);
        thread.start();
    }
}
