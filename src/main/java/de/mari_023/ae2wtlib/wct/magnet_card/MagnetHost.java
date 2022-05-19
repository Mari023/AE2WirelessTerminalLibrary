package de.mari_023.ae2wtlib.wct.magnet_card;

import net.minecraft.nbt.CompoundTag;

import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;

import appeng.api.config.FuzzyMode;
import appeng.api.config.IncludeExclude;
import appeng.util.ConfigInventory;
import appeng.util.prioritylist.IPartitionList;

public class MagnetHost {

    public final ConfigInventory pickupConfig = ConfigInventory.configTypes(27, this::updatePickupFilter);
    public final ConfigInventory insertConfig = ConfigInventory.configTypes(27, this::updateInsertFilter);

    private IPartitionList pickupFilter = createFilter(pickupConfig);
    private IPartitionList insertFilter = createFilter(insertConfig);

    private IncludeExclude pickupMode;
    private IncludeExclude insertMode;

    private final CraftingTerminalHandler ctHandler;

    public MagnetHost(CraftingTerminalHandler ctHandler) {
        this.ctHandler = ctHandler;
        CompoundTag tag = getTag();
        pickupConfig.readFromChildTag(tag, "pickupConfig");
        insertConfig.readFromChildTag(tag, "insertConfig");
        pickupMode = booleanToIncludeExclude(tag.getBoolean("pickupMode"));
        insertMode = booleanToIncludeExclude(tag.getBoolean("insertMode"));
    }

    private IPartitionList createFilter(ConfigInventory config) {
        IPartitionList.Builder builder = IPartitionList.builder();
        builder.fuzzyMode(FuzzyMode.IGNORE_ALL);
        for (int x = 0; x < config.size(); x++) {
            builder.add(config.getKey(x));
        }
        return builder.build();
    }

    private void updatePickupFilter() {
        pickupFilter = createFilter(pickupConfig);
        pickupConfig.writeToChildTag(ctHandler.getCraftingTerminal().getOrCreateTag(), "pickupConfig");
    }

    private void updateInsertFilter() {
        insertFilter = createFilter(insertConfig);
        insertConfig.writeToChildTag(ctHandler.getCraftingTerminal().getOrCreateTag(), "insertConfig");
    }

    public IPartitionList getPickupFilter() {
        return pickupFilter;
    }

    public IncludeExclude getPickupMode() {
        return pickupMode;
    }

    public void togglePickupMode() {
        pickupMode = toggle(pickupMode);
        getTag().putBoolean("pickupMode", includeExcludeToBoolean(pickupMode));
    }

    public IPartitionList getInsertFilter() {
        return insertFilter;
    }

    public IncludeExclude getInsertMode() {
        return insertMode;
    }

    public void toggleInsertMode() {
        insertMode = toggle(insertMode);
        getTag().putBoolean("insertMode", includeExcludeToBoolean(insertMode));
    }

    public CompoundTag getTag() {
        return ctHandler.getCraftingTerminal().getOrCreateTag();
    }

    public IncludeExclude toggle(IncludeExclude includeExclude) {
        return switch (includeExclude) {
            case WHITELIST -> IncludeExclude.BLACKLIST;
            case BLACKLIST -> IncludeExclude.WHITELIST;
        };
    }

    public boolean includeExcludeToBoolean(IncludeExclude includeExclude) {
        return switch (includeExclude) {
            case WHITELIST -> true;
            case BLACKLIST -> false;
        };
    }

    public IncludeExclude booleanToIncludeExclude(boolean b) {
        return b ? IncludeExclude.WHITELIST : IncludeExclude.BLACKLIST;
    }

    public void copyUp() {
        pickupConfig.readFromChildTag(getTag(), "insertConfig");
    }

    public void copyDown() {
        insertConfig.readFromChildTag(getTag(), "pickupConfig");
    }

    public void switchInsertPickup() {
        pickupConfig.writeToChildTag(ctHandler.getCraftingTerminal().getOrCreateTag(), "insertConfig");
        insertConfig.writeToChildTag(ctHandler.getCraftingTerminal().getOrCreateTag(), "pickupConfig");

        pickupConfig.readFromChildTag(getTag(), "pickupConfig");
        insertConfig.readFromChildTag(getTag(), "insertConfig");
    }
}
