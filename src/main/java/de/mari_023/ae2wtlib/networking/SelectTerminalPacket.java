package de.mari_023.ae2wtlib.networking;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import appeng.menu.AEBaseMenu;
import appeng.menu.locator.ItemMenuHostLocator;

import de.mari_023.ae2wtlib.api.AE2wtlibAPI;
import de.mari_023.ae2wtlib.api.registration.WTDefinition;
import de.mari_023.ae2wtlib.api.terminal.ItemWUT;
import de.mari_023.ae2wtlib.api.terminal.WUTHandler;

public record SelectTerminalPacket(WTDefinition terminal) implements AE2wtlibPacket {
    public static final Type<SelectTerminalPacket> ID = new Type<>(AE2wtlibAPI.id("select_terminal"));
    public static final StreamCodec<ByteBuf, SelectTerminalPacket> STREAM_CODEC = WTDefinition.STREAM_CODEC
            .map(SelectTerminalPacket::new, SelectTerminalPacket::terminal);

    public void processPacketData(Player player) {
        if (!(player.containerMenu instanceof AEBaseMenu aeMenu))
            return;

        if (!(aeMenu.getLocator() instanceof ItemMenuHostLocator locator))
            return;

        ItemStack item = locator.locateItem(player);
        if (!(item.getItem() instanceof ItemWUT))
            return;
        if (!WUTHandler.hasTerminal(item, terminal()))
            return;

        WUTHandler.setCurrentTerminal(player, locator, item, terminal());
        WUTHandler.open(player, locator, true);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
