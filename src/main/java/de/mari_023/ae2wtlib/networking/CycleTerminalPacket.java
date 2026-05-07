package de.mari_023.ae2wtlib.networking;

import org.jspecify.annotations.Nullable;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import appeng.menu.AEBaseMenu;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.menu.locator.MenuLocators;

import de.mari_023.ae2wtlib.api.AE2wtlibAPI;
import de.mari_023.ae2wtlib.api.AE2wtlibComponents;
import de.mari_023.ae2wtlib.api.TextConstants;
import de.mari_023.ae2wtlib.api.terminal.ItemWUT;
import de.mari_023.ae2wtlib.api.terminal.WUTHandler;

public record CycleTerminalPacket(boolean isRightClick) implements AE2wtlibPacket {
    public static final Type<CycleTerminalPacket> ID = new Type<>(AE2wtlibAPI.id("cycle_terminal"));
    public static final StreamCodec<ByteBuf, CycleTerminalPacket> STREAM_CODEC = ByteBufCodecs.BOOL
            .map(CycleTerminalPacket::new, CycleTerminalPacket::isRightClick);

    public void processPacketData(Player player) {
        final AbstractContainerMenu containerMenu = player.containerMenu;

        if (!(containerMenu instanceof AEBaseMenu aeMenu)) {
            cycleHeldUniversalTerminal(player);
            return;
        }

        if (!(aeMenu.getLocator() instanceof ItemMenuHostLocator locator))
            return;
        ItemStack item = locator.locateItem(player);

        if (!(item.getItem() instanceof ItemWUT))
            return;

        WUTHandler.cycle(player, locator, item, isRightClick());

        WUTHandler.open(player, locator, true);
    }

    private void cycleHeldUniversalTerminal(Player player) {
        ItemMenuHostLocator locator = getHeldUniversalTerminalLocator(player);
        if (locator == null)
            return;

        ItemStack item = locator.locateItem(player);

        if (!(item.getItem() instanceof ItemWUT))
            return;

        WUTHandler.cycle(player, locator, item, isRightClick());

        var terminal = item.get(AE2wtlibComponents.CURRENT_TERMINAL);
        if (terminal != null)
            player.sendOverlayMessage(TextConstants.currentTerminal(terminal));
    }

    @Nullable
    private static ItemMenuHostLocator getHeldUniversalTerminalLocator(Player player) {
        if (player.getMainHandItem().getItem() instanceof ItemWUT)
            return MenuLocators.forInventorySlot(player.getInventory().getSelectedSlot());
        if (player.getOffhandItem().getItem() instanceof ItemWUT)
            return MenuLocators.forInventorySlot(Inventory.SLOT_OFFHAND);
        return null;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
