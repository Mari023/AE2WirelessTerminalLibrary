package de.mari_023.ae2wtlib.api.results;

public interface Result {
    Status status();

    default boolean valid() {
        return status().isValid();
    }

    default boolean invalid() {
        return !status().isValid();
    }
}
