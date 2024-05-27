package de.mari_023.ae2wtlib.wut;

public class WTDefinitions {
    private WTDefinitions() {
    }

    static {
        if (!AddTerminalEvent.hasRun())
            throw new IllegalStateException("Calling WTDefinitions before terminal registration happened");
    }

    public static WTDefinition CRAFTING = WTDefinition.of("crafting");
    public static WTDefinition PATTERN_ENCODING = WTDefinition.of("pattern_encoding");
    public static WTDefinition PATTERN_ACCESS = WTDefinition.of("pattern_access");
}
