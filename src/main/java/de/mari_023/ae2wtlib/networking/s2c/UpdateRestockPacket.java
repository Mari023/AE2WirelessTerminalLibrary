package de.mari_023.ae2wtlib.networking.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import de.mari_023.ae2wtlib.networking.AE2wtlibPacket;

public class UpdateRestockPacket extends AE2wtlibPacket {

    public static final String NAME = "update_restock";

    public UpdateRestockPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    public UpdateRestockPacket(int slot, int amount) {
        super(createBuffer());
        buf.writeInt(slot);
        buf.writeInt(amount);
    }

    @Override
    public void processPacketData(Player player) {
        player.getInventory().getItem(buf.readInt()).setCount(buf.readInt());
    }

    @Override
    public String getPacketName() {
        return NAME;
    }
}
