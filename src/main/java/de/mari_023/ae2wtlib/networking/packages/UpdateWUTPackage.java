package de.mari_023.ae2wtlib.networking.packages;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.networking.AE2wtlibPacket;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;

import appeng.menu.locator.MenuLocator;
import appeng.menu.locator.MenuLocators;

public record UpdateWUTPackage(MenuLocator locator, @Nullable CompoundTag tag) implements AE2wtlibPacket {

    public static final ResourceLocation ID = AE2wtlib.makeID("update_wut");

    public UpdateWUTPackage(FriendlyByteBuf buf) {
        this(MenuLocators.readFromPacket(buf), buf.readNbt());
    }

    public void processPacketData(Player player) {
        WTMenuHost host = locator.locate(player, WTMenuHost.class);
        if (host != null)
            host.getItemStack().setTag(tag);
        CraftingTerminalHandler.getCraftingTerminalHandler(player).invalidateCache();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        MenuLocators.writeToPacket(buf, locator);
        buf.writeNbt(tag);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
