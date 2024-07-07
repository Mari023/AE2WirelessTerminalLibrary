package de.mari_023.ae2wtlib.wut;

import de.mari_023.ae2wtlib.api.registration.AddTerminalEvent;
import de.mari_023.ae2wtlib.api.registration.WTDefinition;

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
    public static final WTDefinition CRAFTING = WTDefinition.of("crafting");
    /**
     * Wireless Pattern Encoding Terminal
     * <p>
     * Don't access until terminal registration happened, or it WILL crash.
     */
    public static final WTDefinition PATTERN_ENCODING = WTDefinition.of("pattern_encoding");
    /**
     * Wireless Pattern Access Terminal
     * <p>
     * Don't access until terminal registration happened, or it WILL crash.
     */
    public static final WTDefinition PATTERN_ACCESS = WTDefinition.of("pattern_access");
}
