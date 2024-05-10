package de.mari_023.ae2wtlib.datagen;

import java.io.IOException;

@FunctionalInterface
public interface IORunnable {
    void run() throws IOException;

    default void safeRun() {
        try {
            run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
