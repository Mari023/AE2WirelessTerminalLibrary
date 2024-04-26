package de.mari_023.ae2wtlib.networking;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import appeng.menu.locator.ItemMenuHostLocator;
import appeng.menu.locator.MenuLocators;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;

public record UpdateWUTPackage(ItemMenuHostLocator locator, DataComponentPatch patch) implements AE2wtlibPacket {
    public static final Type<UpdateWUTPackage> ID = new Type<>(AE2wtlib.id("update_wut"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateWUTPackage> STREAM_CODEC = StreamCodec
            .ofMember(UpdateWUTPackage::write,
                    UpdateWUTPackage::new);

    public UpdateWUTPackage(ItemMenuHostLocator locator, ItemStack stack) {
        this(locator, stack.getComponentsPatch());
    }

    public UpdateWUTPackage(FriendlyByteBuf buf) {
        this((ItemMenuHostLocator) MenuLocators.readFromPacket(buf), buf.readNbt());
    }

    public void processPacketData(Player player) {
        WTMenuHost host = locator().locate(player, WTMenuHost.class);
        if (host != null)
            host.getItemStack().applyComponents(patch());
    }

    public void write(FriendlyByteBuf buf) {
        MenuLocators.writeToPacket(buf, locator());
        buf.writeNbt(tag);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
