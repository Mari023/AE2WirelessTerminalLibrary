package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.items.tools.powered.WirelessCraftingTerminalItem;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.menu.locator.MenuLocators;
import de.mari_023.fabric.ae2wtlib.AE2wtlibConfig;
import de.mari_023.fabric.ae2wtlib.terminal.IUniversalWirelessTerminalItem;
import de.mari_023.fabric.ae2wtlib.trinket.TrinketLocator;
import de.mari_023.fabric.ae2wtlib.trinket.TrinketsHelper;
import de.mari_023.fabric.ae2wtlib.wct.WCTMenu;
import de.mari_023.fabric.ae2wtlib.wct.WCTMenuHost;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
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

    public MenuType<?> getMenuType(ItemStack stack) {
        return getMenuType();
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
        if(inventorySlot >= 100 && inventorySlot < 200 && AE2wtlibConfig.INSTANCE.allowTrinket())
            return tryOpen(player, new TrinketLocator(inventorySlot), TrinketsHelper.getTrinketsInventory(player).getStackInSlot(inventorySlot - 100));
        else
            return tryOpen(player, MenuLocators.forInventorySlot(inventorySlot), player.getInventory().getItem(inventorySlot));
    }

    /*@Nullable
    public ItemMenuHost getMenuHost(Player player, MenuLocator locator, ItemStack stack) {
        Integer slot = null;
        if(locator instanceof TrinketLocator trinketLocator) slot = trinketLocator.itemIndex();

        return new WCTMenuHost(player, slot, stack, (p, subMenu) -> tryOpen(p, locator, stack));
    }*/

    @Nullable
    public ItemMenuHost getMenuHost(Player player, int slot, ItemStack stack, @Nullable BlockPos pos) {

        return new WCTMenuHost(player, slot, stack, (p, subMenu) -> tryOpen(player, MenuLocators.forInventorySlot(slot), stack));
    }
}
