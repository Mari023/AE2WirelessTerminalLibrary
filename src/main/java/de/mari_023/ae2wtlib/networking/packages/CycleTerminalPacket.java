package de.mari_023.ae2wtlib.networking.packages;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.networking.AE2wtlibPacket;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.wut.ItemWUT;
import de.mari_023.ae2wtlib.wut.WUTHandler;

import appeng.menu.AEBaseMenu;
import appeng.menu.locator.MenuLocator;

public record CycleTerminalPacket(boolean isRightClick) implements AE2wtlibPacket {

    public static final ResourceLocation ID = AE2wtlib.makeID("cycle_terminal");

    public CycleTerminalPacket(FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

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

        WUTHandler.cycle(player, locator, item, isRightClick());

        WUTHandler.open(player, locator, true);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(isRightClick());
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
