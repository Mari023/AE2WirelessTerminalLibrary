package de.mari_023.ae2wtlib;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import appeng.api.config.Actionable;
import appeng.items.tools.powered.WirelessTerminalItem;

public class AE2WTLibCreativeTab {
    private static final List<ItemStack> items = new ArrayList<>();

    public static void init(Registry<CreativeModeTab> registry) {
        var tab = CreativeModeTab.builder()
                .title(TextConstants.CREATIVE_TAB)
                .icon(() -> new ItemStack(AE2wtlib.UNIVERSAL_TERMINAL))
                .displayItems(AE2WTLibCreativeTab::buildDisplayItems)
                .build();
        Registry.register(registry, new ResourceLocation(AE2wtlib.MOD_NAME, "main"), tab);
    }

    public static synchronized void add(Item item) {
        items.add(new ItemStack(item));
    }

    public static synchronized void addTerminal(WirelessTerminalItem terminal) {
        var stack = new ItemStack(terminal);
        items.add(stack.copy());
        terminal.injectAEPower(stack, terminal.getAEMaxPower(stack), Actionable.MODULATE);
        items.add(stack);
    }

    private static synchronized void buildDisplayItems(CreativeModeTab.ItemDisplayParameters itemDisplayParameters,
            CreativeModeTab.Output output) {
        output.acceptAll(items);
    }
}
