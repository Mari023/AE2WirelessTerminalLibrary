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

    private IncludeExclude pickupMode = IncludeExclude.WHITELIST;// TODO load on create
    private IncludeExclude insertMode = IncludeExclude.WHITELIST;

    private final CraftingTerminalHandler ctHandler;

    public MagnetHost(CraftingTerminalHandler ctHandler) {
        this.ctHandler = ctHandler;
        CompoundTag tag = ctHandler.getCraftingTerminal().getOrCreateTag();
        pickupConfig.readFromChildTag(tag, "pickupConfig");
        insertConfig.readFromChildTag(tag, "insertConfig");
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
    }

    public IPartitionList getInsertFilter() {
        return insertFilter;
    }

    public IncludeExclude getInsertMode() {
        return insertMode;
    }

    public void toggleInsertMode() {
        insertMode = toggle(insertMode);
    }

    public IncludeExclude toggle(IncludeExclude includeExclude) {
        return switch (includeExclude) {
            case WHITELIST:
                yield IncludeExclude.BLACKLIST;
            case BLACKLIST:
                yield IncludeExclude.WHITELIST;
        };
    }
}
