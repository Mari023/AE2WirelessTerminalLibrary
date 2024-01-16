package de.mari_023.ae2wtlib.terminal.results;

import org.jetbrains.annotations.Nullable;

import appeng.api.networking.security.IActionHost;

public record ActionHostResult(@Nullable IActionHost host, Status status) implements Result {
    public static ActionHostResult valid(IActionHost host) {
        return new ActionHostResult(host, Status.Valid);
    }

    public static ActionHostResult invalid(Status status) {
        return new ActionHostResult(null, status);
    }
}
