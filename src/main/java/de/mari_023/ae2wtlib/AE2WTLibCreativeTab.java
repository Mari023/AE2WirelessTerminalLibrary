package de.mari_023.ae2wtlib;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AE2WTLibCreativeTab {
    private static final List<Item> items = new ArrayList<>();

    public static CreativeModeTab INSTANCE;

    public static CreativeModeTab init() {
        INSTANCE = FabricItemGroup.builder(new ResourceLocation(AE2wtlib.MOD_NAME, "general"))
                .icon(() -> new ItemStack(AE2wtlib.UNIVERSAL_TERMINAL))
                .displayItems(AE2WTLibCreativeTab::buildDisplayItems)
                .build();
        return INSTANCE;
    }

    public static void add(Item itemDef) {
        items.add(itemDef);
    }

    private static void buildDisplayItems(FeatureFlagSet featureFlagSet, CreativeModeTab.Output output, boolean opItems) {
        for (var item : items) {
            output.accept(item);
        }
    }
}
