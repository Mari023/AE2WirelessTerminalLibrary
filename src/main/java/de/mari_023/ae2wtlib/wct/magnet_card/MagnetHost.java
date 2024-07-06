package de.mari_023.ae2wtlib.wct.magnet_card;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import appeng.api.config.FuzzyMode;
import appeng.api.config.IncludeExclude;
import appeng.api.stacks.AEKeyType;
import appeng.util.ConfigInventory;
import appeng.util.prioritylist.IPartitionList;

import de.mari_023.ae2wtlib.api.AE2wtlibComponents;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;

public class MagnetHost {
    public final ConfigInventory pickupConfig = ConfigInventory.configTypes(27).changeListener(this::updatePickupFilter)
            .supportedTypes(AEKeyType.items()).build();
    public final ConfigInventory insertConfig = ConfigInventory.configTypes(27).changeListener(this::updateInsertFilter)
            .supportedTypes(AEKeyType.items()).build();

    private IPartitionList pickupFilter = createFilter(pickupConfig);
    private IPartitionList insertFilter = createFilter(insertConfig);

    private final CraftingTerminalHandler ctHandler;

    public MagnetHost(CraftingTerminalHandler ctHandler) {
        this.ctHandler = ctHandler;
        pickupConfig.readFromChildTag(getStack().getOrDefault(AE2wtlibComponents.PICKUP_CONFIG, new CompoundTag()), "",
                ctHandler.player.registryAccess());
        insertConfig.readFromChildTag(getStack().getOrDefault(AE2wtlibComponents.INSERT_CONFIG, new CompoundTag()), "",
                ctHandler.player.registryAccess());
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
        CompoundTag tag = getStack().getOrDefault(AE2wtlibComponents.PICKUP_CONFIG, new CompoundTag());
        pickupConfig.writeToChildTag(tag, "", ctHandler.player.registryAccess());
        getStack().set(AE2wtlibComponents.PICKUP_CONFIG, tag);
    }

    private void updateInsertFilter() {
        insertFilter = createFilter(insertConfig);
        CompoundTag tag = getStack().getOrDefault(AE2wtlibComponents.INSERT_CONFIG, new CompoundTag());
        insertConfig.writeToChildTag(tag, "", ctHandler.player.registryAccess());
        getStack().set(AE2wtlibComponents.INSERT_CONFIG, tag);
    }

    public IPartitionList getPickupFilter() {
        return pickupFilter;
    }

    public IncludeExclude getPickupMode() {
        return getStack().getOrDefault(AE2wtlibComponents.PICKUP_MODE, IncludeExclude.BLACKLIST);
    }

    public void togglePickupMode() {
        getStack().set(AE2wtlibComponents.PICKUP_MODE,
                toggle(getStack().getOrDefault(AE2wtlibComponents.PICKUP_MODE, IncludeExclude.BLACKLIST)));
    }

    public IPartitionList getInsertFilter() {
        return insertFilter;
    }

    public IncludeExclude getInsertMode() {
        return getStack().getOrDefault(AE2wtlibComponents.INSERT_MODE, IncludeExclude.BLACKLIST);
    }

    public void toggleInsertMode() {
        getStack().set(AE2wtlibComponents.INSERT_MODE,
                toggle(getStack().getOrDefault(AE2wtlibComponents.INSERT_MODE, IncludeExclude.BLACKLIST)));
    }

    private ItemStack getStack() {
        return ctHandler.getCraftingTerminal();
    }

    public IncludeExclude toggle(IncludeExclude includeExclude) {
        return switch (includeExclude) {
            case WHITELIST -> IncludeExclude.BLACKLIST;
            case BLACKLIST -> IncludeExclude.WHITELIST;
        };
    }

    public void copyUp() {
        pickupConfig.readFromChildTag(getStack().getOrDefault(AE2wtlibComponents.INSERT_CONFIG, new CompoundTag()), "",
                ctHandler.player.registryAccess());
    }

    public void copyDown() {
        insertConfig.readFromChildTag(getStack().getOrDefault(AE2wtlibComponents.PICKUP_CONFIG, new CompoundTag()), "",
                ctHandler.player.registryAccess());
    }

    public void switchInsertPickup() {
        CompoundTag pickupTag = getStack().getOrDefault(AE2wtlibComponents.PICKUP_CONFIG, new CompoundTag());
        CompoundTag insertTag = getStack().getOrDefault(AE2wtlibComponents.INSERT_CONFIG, new CompoundTag());

        pickupConfig.writeToChildTag(pickupTag, "", ctHandler.player.registryAccess());
        getStack().set(AE2wtlibComponents.INSERT_CONFIG, pickupTag);

        insertConfig.writeToChildTag(insertTag, "", ctHandler.player.registryAccess());
        getStack().set(AE2wtlibComponents.PICKUP_CONFIG, insertTag);

        pickupConfig.readFromChildTag(getStack().getOrDefault(AE2wtlibComponents.PICKUP_CONFIG, new CompoundTag()), "",
                ctHandler.player.registryAccess());
        insertConfig.readFromChildTag(getStack().getOrDefault(AE2wtlibComponents.INSERT_CONFIG, new CompoundTag()), "",
                ctHandler.player.registryAccess());
    }
}
