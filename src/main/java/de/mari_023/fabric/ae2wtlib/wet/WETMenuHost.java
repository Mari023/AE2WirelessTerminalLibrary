package de.mari_023.fabric.ae2wtlib.wet;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.implementations.blockentities.IViewCellStorage;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.stacks.GenericStack;
import appeng.crafting.pattern.IAEPatternDetails;
import appeng.helpers.IPatternTerminalHost;
import appeng.menu.ISubMenu;
import appeng.util.inv.AppEngInternalInventory;
import de.mari_023.fabric.ae2wtlib.AE2wtlib;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.fabric.ae2wtlib.terminal.WTlibInternalInventory;
import java.util.function.BiConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class WETMenuHost extends WTMenuHost implements ISegmentedInventory, IViewCellStorage, IPatternTerminalHost {

    private boolean craftingMode = true;
    private boolean substitute = false;
    private boolean substituteFluids = true;
    private final AppEngInternalInventory crafting;
    private final AppEngInternalInventory output;
    private final AppEngInternalInventory pattern;

    public WETMenuHost(final Player ep, int inventorySlot, final ItemStack is, BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(ep, inventorySlot, is, returnToMainMenu);
        crafting = new WTlibInternalInventory(this, 9, "pattern_crafting", is);
        output = new WTlibInternalInventory(this, 3, "output", is);
        pattern = new WTlibInternalInventory(this, 2, "pattern", is);
    }

    @Override
    public MenuType<?> getType() {
        return WETMenu.TYPE;
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(AE2wtlib.PATTERN_ENCODING_TERMINAL);
    }

    public boolean isCraftingRecipe() {
        return craftingMode;
    }

    @Override
    public InternalInventory getSubInventory(ResourceLocation id) {
        if(id.equals(IPatternTerminalHost.INV_CRAFTING)) return crafting;
        else if(id.equals(IPatternTerminalHost.INV_OUTPUT)) return output;
        else if(id.equals(PATTERNS)) return pattern;
        else return null;
    }

    @Override
    public void saveChanges() {}

    @Override
    public void onChangeInventory(final InternalInventory inv, final int slot, final ItemStack removedStack, final ItemStack newStack) {
        if(inv == pattern && slot == 1) {
            final ItemStack is = pattern.getStackInSlot(1);
            final IPatternDetails details = PatternDetailsHelper.decodePattern(is, getPlayer().level);
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