package de.mari_023.fabric.ae2wtlib.trinket;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.core.AELog;
import appeng.menu.locator.MenuLocator;
import de.mari_023.fabric.ae2wtlib.terminal.IUniversalWirelessTerminalItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record TrinketLocator(int itemIndex) implements MenuLocator {

    @Nullable
    public <T> T locate(Player player, Class<T> hostInterface) {
        ItemStack it = TrinketsHelper.getTrinketsInventory(player).getStackInSlot(itemIndex - 100);
        if(!it.isEmpty()) {
            Item item = it.getItem();
            if(item instanceof IUniversalWirelessTerminalItem guiItem) {
                ItemMenuHost menuHost = guiItem.getMenuHost(player, this, it);
                if(hostInterface.isInstance(menuHost)) return hostInterface.cast(menuHost);

                if(menuHost != null) {
                    AELog.warn("Item in slot %d of %s did not create a compatible menu of type %s: %s", itemIndex - 100, player, hostInterface, menuHost);
                }

                return null;
            }
        }

        AELog.warn("Item in Trinket slot %d of %s is not an IMenuItem: %s", itemIndex - 100, player, it);
        return null;
    }

    public void writeToPacket(FriendlyByteBuf buf) {
        buf.writeInt(itemIndex);
    }

    public static TrinketLocator readFromPacket(FriendlyByteBuf buf) {
        return new TrinketLocator(buf.readInt());
    }
}
