package de.mari_023.ae2wtlib.trinket;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.terminal.IUniversalWirelessTerminalItem;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.core.AELog;
import appeng.menu.locator.MenuLocator;

public record TrinketLocator(String group, String type, int slot) implements MenuLocator {

    @Nullable
    public <T> T locate(Player player, Class<T> hostInterface) {
        ItemStack it = TrinketsHelper.getTrinket(player, group, type, slot);
        if (!it.isEmpty()) {
            Item item = it.getItem();
            if (item instanceof IUniversalWirelessTerminalItem guiItem) {
                ItemMenuHost menuHost = guiItem.getMenuHost(player, this, it);
                if (hostInterface.isInstance(menuHost))
                    return hostInterface.cast(menuHost);

                if (menuHost != null) {
                    AELog.warn(
                            "Item in Trinket group %s type %s slot %d of %s did not create a compatible menu of type %s: %s",
                            group, type, slot, player, hostInterface, menuHost);
                }

                return null;
            }
        }

        AELog.warn("Item in  Trinket group %s type %s slot %d of %s is not an IMenuItem: %s", group, type, slot, player,
                it);
        return null;
    }

    public ItemStack locateItem(Player player) {
        return TrinketsHelper.getTrinket(player, group, type, slot);
    }

    public void writeToPacket(FriendlyByteBuf buf) {
        buf.writeUtf(group);
        buf.writeUtf(type);
        buf.writeInt(slot);
    }

    public static TrinketLocator readFromPacket(FriendlyByteBuf buf) {
        return new TrinketLocator(buf.readUtf(), buf.readUtf(), buf.readInt());
    }
}
