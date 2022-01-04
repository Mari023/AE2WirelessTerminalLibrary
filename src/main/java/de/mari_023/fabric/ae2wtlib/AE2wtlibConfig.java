package de.mari_023.fabric.ae2wtlib;

import appeng.core.AEConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.fabricmc.loader.api.FabricLoader;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
@me.shedaniel.autoconfig.annotation.Config(name = "ae2wtlib")
public class AE2wtlibConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    private double outOfRangePowerMultiplier = 2.0;

    private double magnetCardRange = 16.0;

    @ConfigEntry.Gui.Tooltip(count = 3)
    private boolean enableTrinket = true;

    @ConfigEntry.Gui.Excluded
    public static AE2wtlibConfig INSTANCE;

    @ConfigEntry.Gui.Excluded
    private static boolean trinketPresent, trinketChecked;

    public double getOutOfRangePower() {
        return AEConfig.instance().wireless_getDrainRate(AEConfig.instance().wireless_getMaxRange(64)) * outOfRangePowerMultiplier;
    }

    public boolean allowTrinket() {
        if(!trinketChecked) trinketPresent = isTrinketEnabled() && FabricLoader.getInstance().isModLoaded("trinkets");
        trinketChecked = true;
        return trinketPresent;
    }

    private boolean isTrinketEnabled() {
        return enableTrinket;
    }

    public double magnetCardRange() {
        return magnetCardRange;
    }
}