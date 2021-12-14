package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.items.tools.powered.WirelessCraftingTerminalItem;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.menu.MenuLocator;
import appeng.menu.MenuOpener;
import de.mari_023.fabric.ae2wtlib.terminal.IUniversalWirelessTerminalItem;
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

    @Override
    public boolean open(Player player, MenuLocator locator) {
        return MenuOpener.open(WCTMenu.TYPE, player, locator);
    }

    @Override
    public boolean checkPreconditions(ItemStack item, Player player) {
        return super.checkPreconditions(item, player);
    }

    /**
     * @author Mari_023
     * @reason use my own crafting terminal GUI
     */
    @Nullable
    @Overwrite
    public ItemMenuHost getMenuHost(Player player, int inventorySlot, ItemStack stack, @Nullable BlockPos pos) {
        return new WCTMenuHost(player, inventorySlot, stack, (p, subMenu) -> openFromInventory(p, inventorySlot));
    }
}
