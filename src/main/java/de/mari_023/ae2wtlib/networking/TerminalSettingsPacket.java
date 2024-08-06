package de.mari_023.ae2wtlib.networking;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import appeng.menu.locator.ItemMenuHostLocator;

import de.mari_023.ae2wtlib.AE2wtlibAdditionalComponents;
import de.mari_023.ae2wtlib.api.AE2wtlibAPI;
import de.mari_023.ae2wtlib.api.AE2wtlibComponents;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetMode;

public record TerminalSettingsPacket(ItemMenuHostLocator terminal, boolean pickBlock, boolean restock, boolean magnet,
        boolean pickupToME) implements AE2wtlibPacket {

    public static final Type<TerminalSettingsPacket> ID = new Type<>(AE2wtlibAPI.id("terminal_settings"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TerminalSettingsPacket> STREAM_CODEC = StreamCodec
            .composite(
                    AE2wtlibComponents.MENU_HOST_LOCATOR_STREAM_CODEC, TerminalSettingsPacket::terminal,
                    ByteBufCodecs.BOOL, TerminalSettingsPacket::pickBlock,
                    ByteBufCodecs.BOOL, TerminalSettingsPacket::restock,
                    ByteBufCodecs.BOOL, TerminalSettingsPacket::magnet,
                    ByteBufCodecs.BOOL, TerminalSettingsPacket::pickupToME,
                    TerminalSettingsPacket::new);
    @Override
    public void processPacketData(Player player) {
        var stack = terminal.locateItem(player);
        stack.set(AE2wtlibComponents.PICK_BLOCK, pickBlock);
        stack.set(AE2wtlibComponents.RESTOCK, restock);
        var magnetSettings = stack.getOrDefault(AE2wtlibAdditionalComponents.MAGNET_SETTINGS, MagnetMode.OFF);
        magnetSettings = magnetSettings.set(magnet, pickupToME);
        stack.set(AE2wtlibAdditionalComponents.MAGNET_SETTINGS, magnetSettings);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
