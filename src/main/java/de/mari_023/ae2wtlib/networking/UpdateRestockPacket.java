package de.mari_023.ae2wtlib.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import de.mari_023.ae2wtlib.AE2wtlib;

public record UpdateRestockPacket(int slot, int amount) implements AE2wtlibPacket {

    public static final ResourceLocation ID = AE2wtlib.id("update_restock");
    public UpdateRestockPacket(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readInt());
    }

    @Override
    public void processPacketData(Player player) {
        switch (slot()) {
            case Inventory.INVENTORY_SIZE -> player.getInventory().offhand.get(0).setCount(amount());
            case -1 -> {
            }
            default -> player.getInventory().getItem(slot()).setCount(amount());
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(slot());
        buf.writeInt(amount());
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
