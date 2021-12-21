package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.items.tools.powered.WirelessCraftingTerminalItem;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.menu.MenuLocator;
import appeng.menu.MenuOpener;
import de.mari_023.fabric.ae2wtlib.AE2wtlibConfig;
import de.mari_023.fabric.ae2wtlib.terminal.IUniversalWirelessTerminalItem;
import de.mari_023.fabric.ae2wtlib.trinket.TrinketsHelper;
import de.mari_023.fabric.ae2wtlib.wct.WCTMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = WirelessCraftingTerminalItem.class, remap = false)
public class CraftingTerminalItemMixin extends WirelessTerminalItem implements IUniversalWirelessTerminalItem {

    public CraftingTerminalItemMixin() {
        super(null, null);
    }

    /**
     * @author Mari_023
     * @reason use my own crafting terminal GUI
     */
    @Overwrite
    public MenuType<?> getMenuType() {
        return WCTMenu.TYPE;
    }

    @Override
    public boolean open(Player player, MenuLocator locator) {
        return MenuOpener.open(WCTMenu.TYPE, player, locator);
    }

    @Override
    public boolean checkPreconditions(ItemStack item, Player player) {
        return super.checkPreconditions(item, player);
    }

    /**
     * Open a wireless terminal from a slot in the player inventory, i.e. activated via hotkey.
     *
     * @return True if the menu was opened.
     */
    @Override
    public boolean openFromInventory(Player player, int inventorySlot) {
        ItemStack it;
        if(inventorySlot >= 100 && inventorySlot < 200 && AE2wtlibConfig.INSTANCE.allowTrinket())
            it = TrinketsHelper.getTrinketsInventory(player).getStackInSlot(inventorySlot - 100);
        else it = player.getInventory().getItem(inventorySlot);

        return tryOpen(player, MenuLocator.forInventorySlot(inventorySlot), it);
    }
}
