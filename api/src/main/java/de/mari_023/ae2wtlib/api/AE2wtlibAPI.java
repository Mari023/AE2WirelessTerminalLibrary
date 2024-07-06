package de.mari_023.ae2wtlib.api;

import java.util.Objects;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Unit;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.menu.locator.ItemMenuHostLocator;

public class AE2wtlibAPI {
    @Nullable
    private static AE2wtlibAPI instance;

    protected AE2wtlibAPI() {
        if (instance != null)
            throw new IllegalStateException("Already initialized");
        instance = this;
    }

    public static AE2wtlibAPI instance() {
        return Objects.requireNonNull(instance);
    }

    static {
        if (!isModPresent("ae2wtlib"))
            new AE2wtlibAPI();
    }

    public static boolean isModPresent(String mod) {
        return ModList.get().isLoaded(mod);
    }

    public static final String MOD_NAME = "ae2wtlib";
    public static final String API_MOD_NAME = "ae2wtlib_api";

    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_NAME, name);
    }

    public boolean hasQuantumBridgeCard(Supplier<IUpgradeInventory> upgrades) {
        return false;
    }

    public boolean isUniversalTerminal(Item item) {
        return false;
    }

    public ItemStack makeWUT(DataComponentType<Unit> componentType) {
        return ItemStack.EMPTY;
    }

    @ApiStatus.Internal
    public void cycleTerminal(boolean isHandlingRightClick) {}

    /**
     * Sends an update to the client about the current terminal. This is only relevant for Universal Terminals, and only
     * sent when ae2wtlib is present.
     *
     * @param player  The server player.
     * @param locator The menu locator.
     * @param stack   The compound tag containing terminal data.
     */
    public void updateClientTerminal(ServerPlayer player, ItemMenuHostLocator locator, ItemStack stack) {}
}
