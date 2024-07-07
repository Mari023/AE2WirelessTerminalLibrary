package de.mari_023.ae2wtlib.api;

import java.util.Objects;
import java.util.function.Supplier;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.menu.locator.ItemMenuHostLocator;

@ApiStatus.Internal
public class AE2wtlibAPIImpl {
    @Nullable
    private static AE2wtlibAPIImpl instance;

    protected AE2wtlibAPIImpl() {
        if (instance != null)
            throw new IllegalStateException("Already initialized");
        instance = this;
    }

    public static AE2wtlibAPIImpl instance() {
        return Objects.requireNonNull(instance);
    }

    static {
        if (!AE2wtlibAPI.isModPresent("ae2wtlib"))
            new AE2wtlibAPIImpl();
    }

    public boolean hasQuantumBridgeCard(Supplier<IUpgradeInventory> upgrades) {
        return false;
    }

    public boolean isUniversalTerminal(Item item) {
        return false;
    }

    public Item getWUT() {
        return Items.AIR;
    }

    @ApiStatus.Internal
    public void cycleTerminal(boolean isHandlingRightClick) {}

    public void updateClientTerminal(ServerPlayer player, ItemMenuHostLocator locator, ItemStack stack) {}
}
