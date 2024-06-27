package de.mari_023.ae2wtlib.wut;

/**
 * Holds links to AE2wtlib wireless terminal definition.
 * <p>
 * Don't access until terminal registration happened, or it WILL crash.
 */
public class WTDefinitions {
    private WTDefinitions() {}

    static {
        if (!AddTerminalEvent.didRun())
            throw new IllegalStateException("Calling WTDefinitions before terminal registration happened");
    }

    /**
     * Wireless Crafting Terminal
     * <p>
     * Don't access until terminal registration happened, or it WILL crash.
     */
    public static WTDefinition CRAFTING = WTDefinition.of("crafting");
    /**
     * Wireless Pattern Encoding Terminal
     * <p>
     * Don't access until terminal registration happened, or it WILL crash.
     */
    public static WTDefinition PATTERN_ENCODING = WTDefinition.of("pattern_encoding");
    /**
     * Wireless Pattern Access Terminal
     * <p>
     * Don't access until terminal registration happened, or it WILL crash.
     */
    public static WTDefinition PATTERN_ACCESS = WTDefinition.of("pattern_access");
}
