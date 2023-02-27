package de.mari_023.ae2wtlib;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AE2WTLibCreativeTab {
    private static final List<Item> items = new ArrayList<>();

    public static CreativeModeTab INSTANCE;

    public static void init() {
        INSTANCE = FabricItemGroup.builder(new ResourceLocation(AE2wtlib.MOD_NAME, "general"))
                .icon(() -> new ItemStack(AE2wtlib.UNIVERSAL_TERMINAL))
                .displayItems(AE2WTLibCreativeTab::buildDisplayItems)
                .build();
    }

    public static void add(Item itemDef) {
        items.add(itemDef);
    }

    private static void buildDisplayItems(CreativeModeTab.ItemDisplayParameters itemDisplayParameters,
            CreativeModeTab.Output output) {
        for (var item : items) {
            output.accept(item);
        }
    }
}
