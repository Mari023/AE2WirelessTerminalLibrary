package de.mari_023.ae2wtlib.terminal.results;

public interface Result {
    Status status();

    default boolean valid() {
        return status().isValid();
    }

    default boolean invalid() {
        return !status().isValid();
    }
}
