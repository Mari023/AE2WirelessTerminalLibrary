package de.mari_023.fabric.ae2wtlib.wpt;

import appeng.api.AEApi;
import appeng.api.features.IWirelessTerminalHandler;
import appeng.api.implementations.blockentities.IViewCellStorage;
import appeng.api.implementations.guiobjects.IPortableCell;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.parts.reporting.PatternTerminalPart;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import de.mari_023.fabric.ae2wtlib.terminal.WTGuiObject;
import de.mari_023.fabric.ae2wtlib.terminal.ae2wtlibInternalInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class WPTGuiObject extends WTGuiObject implements IPortableCell, ISegmentedInventory, IViewCellStorage, InternalInventoryHost {

    private boolean craftingMode = true;
    private boolean substitute = false;
    private final AppEngInternalInventory crafting;
    private final AppEngInternalInventory output;
    private final AppEngInternalInventory pattern;
    private final boolean isRemote;

    public WPTGuiObject(final IWirelessTerminalHandler wh, final ItemStack is, final PlayerEntity ep, int inventorySlot) {
        super(wh, is, ep, inventorySlot);
        isRemote = ep instanceof ServerPlayerEntity;
        crafting = new ae2wtlibInternalInventory(this, 9, "pattern_crafting", is);
        output = new ae2wtlibInternalInventory(this, 3, "output", is);
        pattern = new ae2wtlibInternalInventory(this, 2, "pattern", is);
    }

    @Override
    public ScreenHandlerType<?> getType() {
        return WPTContainer.TYPE;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(ae2wtlib.PATTERN_TERMINAL);
    }

    public boolean isCraftingRecipe() {
        return craftingMode;
    }

    @Override
    public InternalInventory getSubInventory(Identifier id) {
        if(id.equals(PatternTerminalPart.INV_CRAFTING)) return crafting;
        else if(id.equals(PatternTerminalPart.INV_OUTPUT)) return output;
        else if(id.equals(PATTERNS)) return pattern;
        else return null;
    }

    @Override
    public void saveChanges() {}

    @Override
    public void onChangeInventory(InternalInventory inv, int slot, ItemStack removedStack, ItemStack newStack) {
        if(inv == pattern && slot == 1) {
            final ItemStack is = pattern.getStackInSlot(1);
            final ICraftingPatternDetails details = AEApi.crafting().decodePattern(is, getPlayer().world, false);
            if(details != null) {
                setCraftingRecipe(details.isCraftable());
                setSubstitution(details.canSubstitute());

                for(int x = 0; x < crafting.size() && x < details.getSparseInputs().length; x++) {
                    final IAEItemStack item = details.getSparseInputs()[x];
                    crafting.setItemDirect(x, item == null ? ItemStack.EMPTY : item.createItemStack());
                }

                for(int x = 0; x < output.size() && x < details.getSparseOutputs().length; x++) {
                    final IAEItemStack item = details.getSparseOutputs()[x];
                    output.setItemDirect(x, item == null ? ItemStack.EMPTY : item.createItemStack());
                }
            }
        } else if(inv == crafting) fixCraftingRecipes();
    }

    @Override
    public boolean isRemote() {
        return isRemote;
    }

    public void setCraftingRecipe(final boolean craftingMode) {
        this.craftingMode = craftingMode;
        fixCraftingRecipes();
    }

    public boolean isSubstitution() {
        return this.substitute;
    }

    public void setSubstitution(final boolean canSubstitute) {
        this.substitute = canSubstitute;
    }

    private void fixCraftingRecipes() {
        if(craftingMode) for(int x = 0; x < crafting.size(); x++) {
            final ItemStack is = crafting.getStackInSlot(x);
            if(!is.isEmpty()) is.setCount(1);
        }
    }
}