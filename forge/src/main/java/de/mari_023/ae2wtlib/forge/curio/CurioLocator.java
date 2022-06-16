package de.mari_023.ae2wtlib.forge.curio;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.terminal.IUniversalWirelessTerminalItem;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.core.AELog;
import appeng.menu.locator.MenuLocator;

public record CurioLocator(String identifier, int index) implements MenuLocator {

    public CurioLocator(SlotContext slotContext) {
        this(slotContext.identifier(), slotContext.index());
    }

    @Override
    public <T> @Nullable T locate(Player player, Class<T> hostInterface) {
        ItemStack it = locateItem(player);
        if (!it.isEmpty()) {
            Item item = it.getItem();
            if (item instanceof IUniversalWirelessTerminalItem guiItem) {
                ItemMenuHost menuHost = guiItem.getMenuHost(player, this, it);
                if (hostInterface.isInstance(menuHost))
                    return hostInterface.cast(menuHost);

                if (menuHost != null) {
                    AELog.warn(
                            "Item in Curio slot with ID %s and index %s of %s did not create a compatible menu of type %s: %s",
                            identifier, index, player, hostInterface, menuHost);
                }

                return null;
            }
        }

        AELog.warn("Item in Curio slot with ID %s and index %s of %s is not an IMenuItem: %s",
                identifier, index, player, it);
        return null;
    }

    public ItemStack locateItem(Player player) {
        return CuriosApi.getCuriosHelper().findCurios(player, identifier).get(index).stack();
    }

    public void writeToPacket(FriendlyByteBuf buf) {
        buf.writeUtf(identifier);
        buf.writeInt(index);
    }

    public static CurioLocator readFromPacket(FriendlyByteBuf buf) {
        return new CurioLocator(buf.readUtf(), buf.readInt());
    }
}
