package de.mari_023.ae2wtlib.networking.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.networking.AE2wtlibPacket;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.wut.ItemWUT;
import de.mari_023.ae2wtlib.wut.WUTHandler;

import appeng.menu.AEBaseMenu;
import appeng.menu.locator.MenuLocator;

public class CycleTerminalPacket extends AE2wtlibPacket {

    public static final String NAME = "cycle_terminal";

    public CycleTerminalPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    public CycleTerminalPacket(boolean isRightClick) {
        super(createBuffer());
        buf.writeBoolean(isRightClick);
    }

    @Override
    public void processPacketData(Player player) {
        final AbstractContainerMenu containerMenu = player.containerMenu;

        if (!(containerMenu instanceof AEBaseMenu aeMenu))
            return;

        final MenuLocator locator = aeMenu.getLocator();
        WTMenuHost host = locator.locate(player, WTMenuHost.class);
        if (host == null)
            return;
        ItemStack item = host.getItemStack();

        if (!(item.getItem() instanceof ItemWUT))
            return;

        WUTHandler.cycle(player, locator, item, buf.readBoolean());

        WUTHandler.open(player, locator);
    }

    @Override
    public String getPacketName() {
        return NAME;
    }
}
