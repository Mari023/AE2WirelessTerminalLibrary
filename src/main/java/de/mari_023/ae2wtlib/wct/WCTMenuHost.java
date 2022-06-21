package de.mari_023.ae2wtlib.wct;

import java.util.function.BiConsumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;

import appeng.api.implementations.blockentities.IViewCellStorage;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.core.definitions.AEItems;
import appeng.menu.ISubMenu;
import appeng.parts.reporting.CraftingTerminalPart;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;

public class WCTMenuHost extends WTMenuHost implements IViewCellStorage, ISegmentedInventory, InternalInventoryHost {
    private final AppEngInternalInventory craftingGrid = new AppEngInternalInventory(this, 9);
    private final AppEngInternalInventory trash = new AppEngInternalInventory(this, 1);
    public static final ResourceLocation INV_TRASH = new ResourceLocation(AE2wtlib.MOD_NAME, "wct_trash");

    public WCTMenuHost(final Player ep, @Nullable Integer inventorySlot, final ItemStack is,
            BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(ep, inventorySlot, is, returnToMainMenu);
        readFromNbt();
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(AEItems.WIRELESS_CRAFTING_TERMINAL);
    }

    @Nullable
    @Override
    public InternalInventory getSubInventory(ResourceLocation id) {
        if (id.equals(CraftingTerminalPart.INV_CRAFTING))
            return craftingGrid;
        if (id.equals(INV_TRASH))
            return trash;
        return super.getSubInventory(id);
    }

    @Override
    protected void readFromNbt() {
        super.readFromNbt();
        CompoundTag tag = getItemStack().getOrCreateTag();
        craftingGrid.readFromNBT(tag, "craftingGrid");
        trash.readFromNBT(tag, "trash");
    }

    @Override
    public void saveChanges() {
        super.saveChanges();
        CompoundTag tag = getItemStack().getOrCreateTag();
        craftingGrid.writeToNBT(tag, "craftingGrid");
        trash.writeToNBT(tag, "trash");
    }

    @Override
    public void onChangeInventory(InternalInventory inv, int slot) {
        saveChanges();
    }
}
