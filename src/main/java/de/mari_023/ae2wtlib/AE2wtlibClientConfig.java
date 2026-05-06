package de.mari_023.ae2wtlib;

import org.apache.commons.lang3.tuple.Pair;

import net.neoforged.neoforge.common.ModConfigSpec;

public record AE2wtlibClientConfig(ModConfigSpec.BooleanValue alwaysShowTerminalSelectorValue) {
    public static final AE2wtlibClientConfig CONFIG;
    public static final ModConfigSpec SPEC;

    static {
        Pair<AE2wtlibClientConfig, ModConfigSpec> pair = new ModConfigSpec.Builder()
                .configure(AE2wtlibClientConfig::new);
        CONFIG = pair.getLeft();
        SPEC = pair.getRight();
    }

    AE2wtlibClientConfig(ModConfigSpec.Builder builder) {
        this(builder.define("always_show_terminal_selector", false));
    }

    public boolean alwaysShowTerminalSelector() {
        return alwaysShowTerminalSelectorValue().get();
    }
}
