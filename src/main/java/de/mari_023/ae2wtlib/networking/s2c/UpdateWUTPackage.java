package de.mari_023.ae2wtlib.networking.s2c;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import de.mari_023.ae2wtlib.networking.AE2wtlibPacket;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;

import appeng.menu.locator.MenuLocator;
import appeng.menu.locator.MenuLocators;

public class UpdateWUTPackage extends AE2wtlibPacket {

    public static final String NAME = "update_wut";

    public UpdateWUTPackage(FriendlyByteBuf buf) {
        super(buf);
    }

    public UpdateWUTPackage(MenuLocator locator, @Nullable CompoundTag tag) {
        super(createBuffer());
        MenuLocators.writeToPacket(buf, locator);
        buf.writeNbt(tag);
    }

    @Override
    public void processPacketData(Player player) {
        MenuLocator locator = MenuLocators.readFromPacket(buf);
        CompoundTag tag = buf.readNbt();
        WTMenuHost host = locator.locate(player, WTMenuHost.class);
        if (host != null)
            host.getItemStack().setTag(tag);
        CraftingTerminalHandler.getCraftingTerminalHandler(player).invalidateCache();
    }

    @Override
    public String getPacketName() {
        return NAME;
    }
}
