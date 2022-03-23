package de.mari_023.ae2wtlib.forge.curio;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.core.AELog;
import de.mari_023.ae2wtlib.terminal.IUniversalWirelessTerminalItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Player;

import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import appeng.menu.locator.MenuLocator;

public record CurioLocator(SlotContext slotContext) implements MenuLocator {
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
                            "Item in Curio slot %s of %s did not create a compatible menu of type %s: %s",
                            slotContext, player, hostInterface, menuHost);
                }

                return null;
            }
        }

        AELog.warn("Item in Curio slot %s of %s is not an IMenuItem: %s",
                slotContext, player, it);
        return null;
    }

    public ItemStack locateItem(Player player) {
        return CuriosApi.getCuriosHelper().findCurios(player, slotContext.identifier()).get(slotContext.index()).stack();
    }
}
