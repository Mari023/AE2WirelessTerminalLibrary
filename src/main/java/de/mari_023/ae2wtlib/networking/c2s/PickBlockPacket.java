package de.mari_023.ae2wtlib.networking.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.AE2wtlibEvents;
import de.mari_023.ae2wtlib.networking.AE2wtlibPacket;

public class PickBlockPacket extends AE2wtlibPacket {

    public static final String NAME = "pick_block";

    public PickBlockPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    public PickBlockPacket(ItemStack itemStack) {
        super(createBuffer());
        buf.writeItemStack(itemStack, false);
    }

    @Override
    public void processPacketData(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer))
            return;
        AE2wtlibEvents.pickBlock(serverPlayer, buf.readItem());
    }

    @Override
    public String getPacketName() {
        return NAME;
    }
}
