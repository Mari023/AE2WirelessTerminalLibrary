package de.mari_023.ae2wtlib;

import java.util.ArrayDeque;
import java.util.Collection;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import appeng.api.config.Actionable;
import appeng.items.tools.powered.WirelessTerminalItem;

public class AE2WTLibCreativeTab {
    private static final Collection<ItemStack> items = new ArrayDeque<>();

    public static void init(Registry<CreativeModeTab> registry) {
        var tab = CreativeModeTab.builder()
                .title(TextConstants.CREATIVE_TAB)
                .icon(() -> new ItemStack(AE2wtlib.UNIVERSAL_TERMINAL))
                .displayItems(AE2WTLibCreativeTab::buildDisplayItems)
                .build();
        Registry.register(registry, new ResourceLocation(AE2wtlib.MOD_NAME, "main"), tab);
    }

    public static void add(Item item) {
        items.add(new ItemStack(item));
    }

    public static void addTerminal(WirelessTerminalItem terminal) {
        var stack = new ItemStack(terminal);
        items.add(stack.copy());
        terminal.injectAEPower(stack, terminal.getAEMaxPower(stack), Actionable.MODULATE);
        items.add(stack);
    }

    private static void buildDisplayItems(CreativeModeTab.ItemDisplayParameters itemDisplayParameters,
            CreativeModeTab.Output output) {
        output.acceptAll(items);
    }
}
