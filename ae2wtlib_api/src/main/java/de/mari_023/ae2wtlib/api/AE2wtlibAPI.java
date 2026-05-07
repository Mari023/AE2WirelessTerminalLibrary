package de.mari_023.ae2wtlib.api;

import java.util.function.Supplier;

import com.mojang.datafixers.util.Unit;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.neoforged.fml.ModList;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.menu.locator.ItemMenuHostLocator;

import de.mari_023.ae2wtlib.api.registration.WTDefinition;
import de.mari_023.ae2wtlib.api.terminal.ItemWUT;

public class AE2wtlibAPI {
    public static final String MOD_NAME = "ae2wtlib";
    public static final String API_MOD_NAME = "ae2wtlib_api";

    private AE2wtlibAPI() {}

    public static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(MOD_NAME, name);
    }

    public static boolean isModPresent(String mod) {
        return ModList.get().isLoaded(mod);
    }

    public static boolean hasQuantumBridgeCard(Supplier<IUpgradeInventory> upgrades) {
        return AE2wtlibAPIImpl.instance().hasQuantumBridgeCard(upgrades);
    }

    public static boolean isUniversalTerminal(Item item) {
        return AE2wtlibAPIImpl.instance().isUniversalTerminal(item);
    }

    public static boolean isUniversalTerminal(ItemStack stack) {
        return isUniversalTerminal(stack.getItem());
    }

    public static @Nullable ItemStackTemplate makeWUT(DataComponentType<Unit> componentType) {
        if (!(getWUT() instanceof ItemWUT wutItem))
            return null;
        return new ItemStackTemplate(wutItem, DataComponentPatch.builder().set(componentType, Unit.INSTANCE).build());
    }

    public static Item getWUT() {
        return AE2wtlibAPIImpl.instance().getWUT();
    }

    @ApiStatus.Internal
    public static void cycleTerminal(boolean isHandlingRightClick) {
        AE2wtlibAPIImpl.instance().cycleTerminal(isHandlingRightClick);
    }

    @ApiStatus.Internal
    public static void selectTerminal(WTDefinition terminal) {
        AE2wtlibAPIImpl.instance().selectTerminal(terminal);
    }

    @ApiStatus.Internal
    public static boolean alwaysShowTerminalSelector() {
        return AE2wtlibAPIImpl.instance().alwaysShowTerminalSelector();
    }

    /**
     * Sends an update to the client about the current terminal. This is only relevant for Universal Terminals, and only
     * sent when ae2wtlib is present.
     *
     * @param player  The server player.
     * @param locator The menu locator.
     * @param stack   The compound tag containing terminal data.
     */
    public static void updateClientTerminal(ServerPlayer player, ItemMenuHostLocator locator, ItemStack stack) {
        AE2wtlibAPIImpl.instance().updateClientTerminal(player, locator, stack);
    }
}
