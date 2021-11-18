package de.mari_023.fabric.ae2wtlib.wpt;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.implementations.blockentities.IViewCellStorage;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.storage.GenericStack;
import appeng.crafting.pattern.IAEPatternDetails;
import appeng.helpers.IPatternTerminalHost;
import appeng.parts.reporting.PatternTerminalPart;
import appeng.util.inv.AppEngInternalInventory;
import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.terminal.WTGuiObject;
import de.mari_023.fabric.ae2wtlib.terminal.ae2wtlibInternalInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class WPTGuiObject extends WTGuiObject implements ISegmentedInventory, IViewCellStorage, IPatternTerminalHost {

    private boolean craftingMode = true;
    private boolean substitute = false;
    private boolean substituteFluids = true;
    private final AppEngInternalInventory crafting;
    private final AppEngInternalInventory output;
    private final AppEngInternalInventory pattern;

    public WPTGuiObject(final PlayerEntity ep, int inventorySlot, final ItemStack is) {
        super(ep, inventorySlot, is);
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
    public void onChangeInventory(final InternalInventory inv, final int slot, final ItemStack removedStack, final ItemStack newStack) {
        if(inv == pattern && slot == 1) {
            final ItemStack is = pattern.getStackInSlot(1);
            final IPatternDetails details = PatternDetailsHelper.decodePattern(is, getPlayer().world);
            if(details instanceof IAEPatternDetails aeDetails) {
                setCraftingRecipe(aeDetails.isCraftable());
                setSubstitution(aeDetails.canSubstitute());

                for(int x = 0; x < crafting.size() && x < aeDetails.getSparseInputs().length; x++) {
                    crafting.setItemDirect(x, GenericStack.wrapInItemStack(aeDetails.getSparseInputs()[x]));
                }

                for(int x = 0; x < output.size() && x < aeDetails.getSparseOutputs().length; x++) {
                    output.setItemDirect(x, GenericStack.wrapInItemStack(aeDetails.getSparseOutputs()[x]));
                }
            }
        } else if(inv == crafting) fixCraftingRecipes();
    }

    public void setCraftingRecipe(final boolean craftingMode) {
        this.craftingMode = craftingMode;
        ItemWT.setBoolean(getItemStack(), craftingMode, "craftingMode");
        if(!isClientSide()) fixCraftingRecipes();
    }

    public boolean isSubstitution() {
        return substitute;
    }

    public void setSubstitution(final boolean canSubstitute) {
        substitute = canSubstitute;
        ItemWT.setBoolean(getItemStack(), substitute, "substitute");
    }

    public boolean isFluidSubstitution() {
        return substituteFluids;
    }

    public void setFluidSubstitution(final boolean canSubstitute) {
        substituteFluids = canSubstitute;
        ItemWT.setBoolean(getItemStack(), substituteFluids, "substitute_fluids");
    }
}