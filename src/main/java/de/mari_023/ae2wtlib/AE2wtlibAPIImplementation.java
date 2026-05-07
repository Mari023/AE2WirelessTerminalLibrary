package de.mari_023.ae2wtlib;

import java.util.function.Supplier;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.menu.locator.ItemMenuHostLocator;

import de.mari_023.ae2wtlib.api.AE2wtlibAPIImpl;
import de.mari_023.ae2wtlib.api.registration.WTDefinition;
import de.mari_023.ae2wtlib.networking.SelectTerminalPacket;
import de.mari_023.ae2wtlib.networking.UpdateWUTPackage;

public class AE2wtlibAPIImplementation extends AE2wtlibAPIImpl {
    @Override
    public boolean hasQuantumBridgeCard(Supplier<IUpgradeInventory> upgrades) {
        return upgrades.get().isInstalled(AE2wtlibItems.QUANTUM_BRIDGE_CARD);
    }

    @Override
    public boolean isUniversalTerminal(Item item) {
        return item == AE2wtlibItems.UNIVERSAL_TERMINAL.asItem();
    }

    @Override
    public void selectTerminal(WTDefinition terminal) {
        ClientPacketDistributor.sendToServer(new SelectTerminalPacket(terminal));
    }

    @Override
    public boolean alwaysShowTerminalSelector() {
        return AE2wtlibClientConfig.CONFIG.alwaysShowTerminalSelector();
    }

    @Override
    public void updateClientTerminal(ServerPlayer player, ItemMenuHostLocator locator, ItemStack stack) {
        PacketDistributor.sendToPlayer(player, new UpdateWUTPackage(locator, stack));
    }

    @Override
    public Item getWUT() {
        return AE2wtlibItems.UNIVERSAL_TERMINAL.asItem();
    }
}
