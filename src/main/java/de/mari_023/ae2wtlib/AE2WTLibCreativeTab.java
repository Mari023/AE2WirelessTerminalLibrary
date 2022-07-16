package de.mari_023.ae2wtlib;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;

public class AE2WTLibCreativeTab {
    private static final List<Item> items = new ArrayList<>();

    public static CreativeModeTab INSTANCE;

    public static void init(CreativeModeTabEvent.Register register) {
        INSTANCE = register.registerCreativeModeTab(new ResourceLocation(AE2wtlib.MOD_NAME, "general"),
                builder -> builder.title(Component.translatable("itemGroup.ae2wtlib.general"))
                        .icon(() -> new ItemStack(AE2wtlib.UNIVERSAL_TERMINAL))
                        .displayItems(AE2WTLibCreativeTab::buildDisplayItems)
                        .build());
    }

    public static void add(Item itemDef) {
        items.add(itemDef);
    }

    private static void buildDisplayItems(FeatureFlagSet featureFlagSet, CreativeModeTab.Output output,
            boolean opItems) {
        for (var item : items) {
            output.accept(item);
        }
    }
}
