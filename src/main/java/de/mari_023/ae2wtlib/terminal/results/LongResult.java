package de.mari_023.ae2wtlib.terminal.results;

public record LongResult(long result, Status status) implements Result {
    public static LongResult valid(long result) {
        return new LongResult(result, Status.Valid);
    }

    public static LongResult invalid(Status status) {
        return new LongResult(0, status);
    }
}
