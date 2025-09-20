package de.mari_023.ae2wtlib.wct.magnet_card;

import de.mari_023.ae2wtlib.ValueIOHelper;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;

import appeng.api.config.FuzzyMode;
import appeng.api.config.IncludeExclude;
import appeng.api.stacks.AEKeyType;
import appeng.util.ConfigInventory;
import appeng.util.prioritylist.IPartitionList;

import de.mari_023.ae2wtlib.api.AE2wtlibComponents;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;
import net.minecraft.world.level.storage.TagValueOutput;

import static de.mari_023.ae2wtlib.ValueIOHelper.fromComponent;
import static de.mari_023.ae2wtlib.api.AE2wtlibComponents.INSERT_CONFIG;
import static de.mari_023.ae2wtlib.api.AE2wtlibComponents.PICKUP_CONFIG;

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
        pickupConfig.readFromChildTag(fromComponent(ctHandler.player, getStack(), PICKUP_CONFIG), "");
        insertConfig.readFromChildTag(fromComponent(ctHandler.player, getStack(), INSERT_CONFIG), "");
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
        TagValueOutput tagValueOutput = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        pickupConfig.writeToChildTag(tagValueOutput, "");
        getStack().set(PICKUP_CONFIG, tagValueOutput.buildResult());
    }

    private void updateInsertFilter() {
        insertFilter = createFilter(insertConfig);
        TagValueOutput tagValueOutput = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        insertConfig.writeToChildTag(tagValueOutput, "");
        getStack().set(INSERT_CONFIG, tagValueOutput.buildResult());
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
        pickupConfig.readFromChildTag(fromComponent(ctHandler.player, getStack(), INSERT_CONFIG), "");
    }

    public void copyDown() {
        insertConfig.readFromChildTag(fromComponent(ctHandler.player, getStack(), PICKUP_CONFIG), "");
    }

    public void switchInsertPickup() {
        TagValueOutput pickupTVO = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        TagValueOutput insertTVO = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);

        pickupConfig.writeToChildTag(pickupTVO, "");
        getStack().set(INSERT_CONFIG, pickupTVO.buildResult());

        insertConfig.writeToChildTag(insertTVO, "");
        getStack().set(PICKUP_CONFIG, insertTVO.buildResult());

        pickupConfig.readFromChildTag(ValueIOHelper.fromTag(ctHandler.player, pickupTVO.buildResult()), "");
        insertConfig.readFromChildTag(ValueIOHelper.fromTag(ctHandler.player, insertTVO.buildResult()), "");
    }
}
