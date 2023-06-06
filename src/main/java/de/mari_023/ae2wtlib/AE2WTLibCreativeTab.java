package de.mari_023.ae2wtlib;

import java.util.ArrayList;
import java.util.List;

import appeng.api.config.Actionable;
import appeng.items.tools.powered.WirelessTerminalItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AE2WTLibCreativeTab {
    private static final List<ItemStack> items = new ArrayList<>();

    public static CreativeModeTab init() {
        return FabricItemGroup.builder()
                .title(TextConstants.CREATIVE_TAB)
                .icon(() -> new ItemStack(AE2wtlib.UNIVERSAL_TERMINAL))
                .displayItems(AE2WTLibCreativeTab::buildDisplayItems)
                .build();
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
