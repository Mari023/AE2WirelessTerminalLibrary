package de.mari_023.fabric.ae2wtlib.wpt;

import alexiil.mc.lib.attributes.item.FixedItemInv;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IStorageMonitorable;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.container.slot.IOptionalSlotHost;
import appeng.container.slot.PatternTermSlot;
import appeng.core.Api;
import appeng.core.sync.BasePacket;
import appeng.core.sync.packets.PatternSlotPacket;
import appeng.helpers.IContainerCraftingPacket;
import de.mari_023.fabric.ae2wtlib.FixedEmptyInventory;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;

public class WirelessPatternTermSlot extends PatternTermSlot {
    public WirelessPatternTermSlot(PlayerEntity player, IActionSource mySrc, IEnergySource energySrc, IStorageMonitorable storage, FixedItemInv cMatrix, FixedItemInv secondMatrix, FixedItemInv output, int x, int y, IOptionalSlotHost h, int groupNumber, IContainerCraftingPacket c) {
        super(player, mySrc, energySrc, storage, cMatrix, secondMatrix, output, x, y, h, groupNumber, c);
    }

    @Override
    public BasePacket getRequest(final boolean shift) {
        FixedItemInv pattern = null;
        try {
            Field f = getClass().getSuperclass().getSuperclass().getDeclaredField("pattern");
            f.setAccessible(true);
            pattern = (FixedItemInv) f.get(this);
            f.setAccessible(false);
        } catch(IllegalAccessException | NoSuchFieldException ignored) {}

        if(pattern == null)
            return new PatternSlotPacket(new FixedEmptyInventory(9), Api.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(getStack()), shift);

        PacketByteBuf buf = PacketByteBufs.create();
        writeItem(Api.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(getStack()), buf);
        buf.writeBoolean(shift);
        for(int x = 0; x < 9; x++) {
            writeItem(Api.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(pattern.getInvStack(x)), buf);
        }
        ClientPlayNetworking.send(new Identifier("ae2wtlib", "patternslotpacket"), buf);

        //This is just a stub. we don't actually use it, but this is the easiest (tho dirty) way to hack our own solution in
        return new PatternSlotPacket(pattern, Api.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(getStack()), shift);
    }

    private void writeItem(final IAEItemStack slotItem, final PacketByteBuf data) {
        if(slotItem == null) {
            data.writeBoolean(false);
        } else {
            data.writeBoolean(true);
            slotItem.writeToPacket(data);
        }
    }
}