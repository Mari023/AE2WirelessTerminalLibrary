package de.mari_023.fabric.ae2wtlib.terminal;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;
import de.mari_023.fabric.ae2wtlib.trinket.TrinketLocator;
import de.mari_023.fabric.ae2wtlib.wut.WUTHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IUniversalWirelessTerminalItem extends IInfinityBoosterCardHolder {
    default boolean open(final Player player, ItemStack stack, final MenuLocator locator) {
        return MenuOpener.open(getMenuType(stack), player, locator);
    }

    default boolean tryOpen(Player player, MenuLocator locator, ItemStack stack) {
        if(checkPreconditions(stack, player)) return open(player, stack, locator);
        return false;
    }

    @Nullable
    default ItemMenuHost getMenuHost(Player player, MenuLocator locator, ItemStack stack) {
        //Integer slot = null;
        //if(locator instanceof MenuItemLocator menuItemLocator) slot = menuItemLocator.itemIndex();//TODO set the slot
        return WUTHandler.wirelessTerminals.get(WUTHandler.getCurrentTerminal(stack)).wTMenuHostFactory().create(player, null, stack, (p, subMenu) -> tryOpen(player, locator, stack));
    }

    MenuType<?> getMenuType(ItemStack stack);

    boolean checkPreconditions(ItemStack item, Player player);

    default boolean hasBoosterCard(ItemStack hostItem) {
        return getBoosterCard(hostItem).getItem() instanceof ItemInfinityBooster;
    }

    default void setBoosterCard(ItemStack hostItem, ItemStack boosterCard) {
        if(hostItem.getItem() instanceof IInfinityBoosterCardHolder)
            ItemWT.setSavedSlot(hostItem, boosterCard, "boosterCard");
    }

    default ItemStack getBoosterCard(ItemStack hostItem) {
        if(hostItem.getItem() instanceof IInfinityBoosterCardHolder)
            return ItemWT.getSavedSlot(hostItem, "boosterCard");
        return ItemStack.EMPTY;
    }
}
