package de.mari_023.fabric.ae2wtlib.wpt;

import appeng.api.AEApi;
import appeng.api.crafting.IPatternDetails;
import appeng.api.features.IWirelessTerminalHandler;
import appeng.api.implementations.blockentities.IViewCellStorage;
import appeng.api.implementations.guiobjects.IPortableCell;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.storage.StorageChannels;
import appeng.api.storage.data.IAEStack;
import appeng.crafting.pattern.IAEPatternDetails;
import appeng.items.misc.FluidDummyItem;
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
import org.jetbrains.annotations.Nullable;

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
    public void onChangeInventory(final InternalInventory inv, final int slot, final ItemStack removedStack, final ItemStack newStack) {
        if(inv == pattern && slot == 1) {
            final ItemStack is = pattern.getStackInSlot(1);
            final IPatternDetails details = AEApi.patterns().decodePattern(is, getPlayer().world);
            if(details instanceof IAEPatternDetails aeDetails) {
                setCraftingRecipe(aeDetails.isCraftable());
                setSubstitution(aeDetails.canSubstitute());

                for(int x = 0; x < crafting.size() && x < aeDetails.getSparseInputs().length; x++) {
                    crafting.setItemDirect(x, getDisplayStack(aeDetails.getSparseInputs()[x]));
                }

                for(int x = 0; x < output.size() && x < aeDetails.getSparseOutputs().length; x++) {
                    output.setItemDirect(x, getDisplayStack(aeDetails.getSparseOutputs()[x]));
                }
            }
        } else if(inv == crafting) fixCraftingRecipes();
    }

    private ItemStack getDisplayStack(@Nullable IAEStack aeStack) {
        if(aeStack == null) {
            return ItemStack.EMPTY;
        } else if(aeStack.getChannel() == StorageChannels.items()) {
            return aeStack.cast(StorageChannels.items()).createItemStack();
        } else if(aeStack.getChannel() == StorageChannels.fluids()) {
            return FluidDummyItem.fromFluidStack(aeStack.cast(StorageChannels.fluids()).getFluidStack(), true);
        } else {
            throw new IllegalArgumentException("Only item and fluid stacks are supported");
        }
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