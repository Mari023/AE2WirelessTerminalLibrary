package de.mari_023.ae2wtlib.networking.c2s;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import de.mari_023.ae2wtlib.networking.AE2wtlibPacket;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;

import appeng.menu.AEBaseMenu;
import appeng.menu.locator.MenuLocator;

public class TerminalSettingsPacket extends AE2wtlibPacket {

    public static final String NAME = "terminal_settings";

    public TerminalSettingsPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    public TerminalSettingsPacket(CompoundTag tag) {
        super(createBuffer());
        buf.writeNbt(tag);
    }

    @Override
    public void processPacketData(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer))
            return;

        final AbstractContainerMenu containerMenu = player.containerMenu;

        if (!(containerMenu instanceof AEBaseMenu aeMenu))
            return;

        final MenuLocator locator = aeMenu.getLocator();
        WTMenuHost host = locator.locate(player, WTMenuHost.class);
        if (host == null)
            return;
        host.getItemStack().setTag(buf.readNbt());
    }

    @Override
    public String getPacketName() {
        return NAME;
    }
}
