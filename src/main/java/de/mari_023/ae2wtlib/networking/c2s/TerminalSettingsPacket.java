package de.mari_023.ae2wtlib.networking.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.AE2wtlibTags;
import de.mari_023.ae2wtlib.networking.AE2wtlibPacket;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetMode;
import de.mari_023.ae2wtlib.wut.WUTHandler;

import appeng.menu.AEBaseMenu;
import appeng.menu.locator.MenuLocator;

public class TerminalSettingsPacket extends AE2wtlibPacket {

    public static final String NAME = "terminal_settings";

    public TerminalSettingsPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    public TerminalSettingsPacket(boolean pickBlock, boolean restock, boolean magnet,
            boolean pickupToME) {
        super(createBuffer());
        buf.writeBoolean(pickBlock);
        buf.writeBoolean(restock);
        buf.writeBoolean(magnet);
        buf.writeBoolean(pickupToME);
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
        ItemStack item = host.getItemStack();

        item.getOrCreateTag().putBoolean(AE2wtlibTags.PICK_BLOCK, buf.readBoolean());
        item.getOrCreateTag().putBoolean(AE2wtlibTags.RESTOCK, buf.readBoolean());
        var magnetSettings = MagnetMode.fromByte(item.getOrCreateTag().getByte(AE2wtlibTags.MAGNET_SETTINGS));
        magnetSettings = magnetSettings.set(buf.readBoolean(), buf.readBoolean());
        item.getOrCreateTag().putByte(AE2wtlibTags.MAGNET_SETTINGS, magnetSettings.getId());

        WUTHandler.updateClientTerminal(serverPlayer, locator, item.getTag());
    }

    @Override
    public String getPacketName() {
        return NAME;
    }
}
