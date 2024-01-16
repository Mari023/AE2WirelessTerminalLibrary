package de.mari_023.ae2wtlib;

import org.apache.commons.lang3.tuple.Pair;

import net.neoforged.neoforge.common.ModConfigSpec;

public record AE2wtlibConfig(ModConfigSpec.ConfigValue<Double> magnetCardRangeValue) {
    public static final AE2wtlibConfig CONFIG;
    public static final ModConfigSpec SPEC;

    static {
        Pair<AE2wtlibConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(AE2wtlibConfig::new);
        CONFIG = pair.getLeft();
        SPEC = pair.getRight();
    }

    AE2wtlibConfig(ModConfigSpec.Builder builder) {
        this(builder.define("magnet_card_range", 16.0));
    }

    public double magnetCardRange() {
        return magnetCardRangeValue().get();
    }
}
