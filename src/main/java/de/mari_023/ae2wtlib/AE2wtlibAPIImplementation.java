package de.mari_023.ae2wtlib;

import java.util.function.Supplier;

import com.mojang.datafixers.util.Unit;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import appeng.api.config.Actionable;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.menu.locator.ItemMenuHostLocator;

import de.mari_023.ae2wtlib.api.AE2wtlibAPI;
import de.mari_023.ae2wtlib.networking.CycleTerminalPacket;
import de.mari_023.ae2wtlib.networking.UpdateWUTPackage;

public class AE2wtlibAPIImplementation extends AE2wtlibAPI {
    @Override
    public boolean hasQuantumBridgeCard(Supplier<IUpgradeInventory> upgrades) {
        return upgrades.get().isInstalled(AE2wtlibItems.QUANTUM_BRIDGE_CARD);
    }

    @Override
    public boolean isUniversalTerminal(Item item) {
        return item == AE2wtlibItems.UNIVERSAL_TERMINAL;
    }

    @Override
    public void cycleTerminal(boolean isHandlingRightClick) {
        PacketDistributor.sendToServer(new CycleTerminalPacket(isHandlingRightClick));
    }

    @Override
    public void updateClientTerminal(ServerPlayer player, ItemMenuHostLocator locator, ItemStack stack) {
        PacketDistributor.sendToPlayer(player, new UpdateWUTPackage(locator, stack));
    }

    @Override
    public ItemStack makeWUT(DataComponentType<Unit> componentType) {
        ItemStack wut = new ItemStack(AE2wtlibItems.UNIVERSAL_TERMINAL);

        wut.set(componentType, Unit.INSTANCE);
        AE2wtlibItems.UNIVERSAL_TERMINAL.injectAEPower(wut,
                AE2wtlibItems.UNIVERSAL_TERMINAL.getAEMaxPower(wut), Actionable.MODULATE);
        return wut;
    }
}