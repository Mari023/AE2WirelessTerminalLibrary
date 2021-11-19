package de.mari_023.fabric.ae2wtlib.wct;

import appeng.api.implementations.blockentities.IViewCellStorage;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.core.definitions.AEItems;
import appeng.items.tools.powered.WirelessCraftingTerminalItem;
import appeng.parts.reporting.CraftingTerminalPart;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import de.mari_023.fabric.ae2wtlib.terminal.WTGuiObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class WCTGuiObject extends WTGuiObject implements IViewCellStorage, ISegmentedInventory, InternalInventoryHost {
    private final AppEngInternalInventory craftingGrid = new AppEngInternalInventory(this, 9);

    public WCTGuiObject(final PlayerEntity ep, int inventorySlot, final ItemStack is) {
        super(ep, inventorySlot, is);
        craftingGrid.readFromNBT(getItemStack().getOrCreateNbt(), "craftingGrid");
    }

    @Override
    public ScreenHandlerType<?> getType() {
        return WCTContainer.TYPE;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(AEItems.WIRELESS_CRAFTING_TERMINAL);
    }

    @Nullable
    @Override
    public InternalInventory getSubInventory(Identifier id) {
        if(id.equals(CraftingTerminalPart.INV_CRAFTING)) return craftingGrid;
        else return null;
    }

    @Override
    public void saveChanges() {
        craftingGrid.writeToNBT(getItemStack().getOrCreateNbt(), "craftingGrid");
    }

    @Override
    public void onChangeInventory(InternalInventory inv, int slot, ItemStack removedStack, ItemStack newStack) {}
}