package de.mari_023.ae2wtlib.terminal.results;

import javax.annotation.Nullable;

import appeng.api.networking.IGrid;

public record GridResult(@Nullable IGrid grid, Status status) implements Result {
    public static GridResult valid(IGrid grid) {
        return new GridResult(grid, Status.Valid);
    }

    public static GridResult invalid(Status status) {
        return new GridResult(null, status);
    }
}
