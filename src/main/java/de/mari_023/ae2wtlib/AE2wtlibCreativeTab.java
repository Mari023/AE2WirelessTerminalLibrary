package de.mari_023.ae2wtlib;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import appeng.api.config.Actionable;
import appeng.items.tools.powered.WirelessTerminalItem;

import de.mari_023.ae2wtlib.api.AE2wtlibAPI;
import de.mari_023.ae2wtlib.api.TextConstants;
import de.mari_023.ae2wtlib.api.registration.WTDefinition;
import de.mari_023.ae2wtlib.api.terminal.ItemWUT;
import de.mari_023.ae2wtlib.wut.recipe.Common;

public class AE2wtlibCreativeTab {
    private static final List<ItemStack> items = new ArrayList<>();

    public static void init() {
        var tab = CreativeModeTab.builder()
                .title(TextConstants.CREATIVE_TAB)
                .icon(() -> {
                    var terminal = AE2wtlibItems.UNIVERSAL_TERMINAL;
                    var stack = new ItemStack(terminal);
                    terminal.injectAEPower(stack, terminal.getAEMaxPower(stack), Actionable.MODULATE);
                    return stack;
                })
                .displayItems(AE2wtlibCreativeTab::buildDisplayItems)
                .build();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, AE2wtlibAPI.id("main"), tab);
    }

    public static boolean registrationHappened() {
        return !items.isEmpty();
    }

    public static synchronized void add(Item item) {
        items.add(new ItemStack(item));
    }

    public static synchronized void addTerminal(WirelessTerminalItem terminal) {
        var stack = new ItemStack(terminal);
        items.add(stack.copy());
        terminal.injectAEPower(stack, terminal.getAEMaxPower(stack), Actionable.MODULATE);
        items.add(stack.copy());
    }

    public static synchronized void addUniversalTerminal(ItemWUT wut) {
        var stack = new ItemStack(wut);
        items.add(stack.copy());
        for (var terminal : WTDefinition.wirelessTerminals()) {
            Common.mergeTerminal(stack, new ItemStack(terminal.item()), terminal);
        }
        wut.injectAEPower(stack, wut.getAEMaxPower(stack), Actionable.MODULATE);
        items.add(stack.copy());
    }

    private static synchronized void buildDisplayItems(CreativeModeTab.ItemDisplayParameters itemDisplayParameters,
            CreativeModeTab.Output output) {
        output.acceptAll(items);
    }
}
