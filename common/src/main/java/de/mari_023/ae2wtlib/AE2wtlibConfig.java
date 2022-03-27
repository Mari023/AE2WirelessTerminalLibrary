package de.mari_023.ae2wtlib;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

import appeng.core.AEConfig;

@SuppressWarnings({ "FieldCanBeLocal", "FieldMayBeFinal" })
@me.shedaniel.autoconfig.annotation.Config(name = "de/mari_023/ae2wtlib")
public class AE2wtlibConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    private double outOfRangePowerMultiplier = 2.0;

    private double magnetCardRange = 16.0;

    @ConfigEntry.Gui.Excluded
    public static AE2wtlibConfig INSTANCE;

    public double getOutOfRangePower() {
        return AEConfig.instance().wireless_getDrainRate(AEConfig.instance().wireless_getMaxRange(64))
                * outOfRangePowerMultiplier;
    }

    public double magnetCardRange() {
        return magnetCardRange;
    }

    public static void init() {
        if (INSTANCE != null)
            return;
        AutoConfig.register(AE2wtlibConfig.class, JanksonConfigSerializer::new);
        INSTANCE = AutoConfig.getConfigHolder(AE2wtlibConfig.class).getConfig();
    }
}
