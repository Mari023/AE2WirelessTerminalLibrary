package de.mari_023.ae2wtlib.mixin;

import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.terminal.IUniversalWirelessTerminalItem;
import de.mari_023.ae2wtlib.wct.WCTMenu;
import de.mari_023.ae2wtlib.wct.WCTMenuHost;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.items.tools.powered.WirelessCraftingTerminalItem;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.menu.locator.MenuLocators;

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

    @Nullable
    public ItemMenuHost getMenuHost(Player player, int slot, ItemStack stack, @Nullable BlockPos pos) {

        return new WCTMenuHost(player, slot, stack,
                (p, subMenu) -> tryOpen(player, MenuLocators.forInventorySlot(slot), stack));
    }

    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
        if (level.isClientSide()) return;
        if (!(entity instanceof ServerPlayer player)) return;
        MagnetHandler.handle(player, itemStack);
    }
}
