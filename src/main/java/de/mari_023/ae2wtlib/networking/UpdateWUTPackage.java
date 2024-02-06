package de.mari_023.ae2wtlib.networking;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import appeng.menu.locator.ItemMenuHostLocator;
import appeng.menu.locator.MenuLocators;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;

public record UpdateWUTPackage(ItemMenuHostLocator locator, @Nullable CompoundTag tag) implements AE2wtlibPacket {

    public static final ResourceLocation ID = AE2wtlib.id("update_wut");
    public UpdateWUTPackage(FriendlyByteBuf buf) {
        this((ItemMenuHostLocator) MenuLocators.readFromPacket(buf), buf.readNbt());
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
