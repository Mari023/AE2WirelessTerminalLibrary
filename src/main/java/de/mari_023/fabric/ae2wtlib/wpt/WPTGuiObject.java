package de.mari_023.fabric.ae2wtlib.wpt;

import alexiil.mc.lib.attributes.item.FixedItemInv;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.implementations.guiobjects.IPortableCell;
import appeng.api.implementations.tiles.IViewCellStorage;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.core.Api;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.inv.IAEAppEngInventory;
import appeng.util.inv.InvOperation;
import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import de.mari_023.fabric.ae2wtlib.terminal.WTGuiObject;
import de.mari_023.fabric.ae2wtlib.terminal.ae2wtlibInternalInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class WPTGuiObject extends WTGuiObject implements IPortableCell, IAEAppEngInventory, IViewCellStorage {

    private boolean craftingMode = true;
    private boolean substitute = false;
    private final AppEngInternalInventory crafting;
    private final AppEngInternalInventory output;
    private final AppEngInternalInventory pattern;
    private static final ItemStack ICON = new ItemStack(ae2wtlib.PATTERN_TERMINAL);

    public WPTGuiObject(final IWirelessTermHandler wh, final ItemStack is, final PlayerEntity ep, int inventorySlot) {
        super(wh, is, ep, inventorySlot, WPTContainer.TYPE);
        crafting = new ae2wtlibInternalInventory(this, 9, "pattern_crafting", is);
        output = new ae2wtlibInternalInventory(this, 3, "output", is);
        pattern = new ae2wtlibInternalInventory(this, 2, "pattern", is);
    }

    public boolean isCraftingRecipe() {
        return craftingMode;
    }

    public FixedItemInv getInventoryByName(final String name) {
        if(name.equals("crafting")) return crafting;

        if(name.equals("output")) return output;

        if(name.equals("pattern")) return pattern;
        return null;
    }

    @Override
    public void saveChanges() {}

    @Override
    public void onChangeInventory(FixedItemInv inv, int slot, InvOperation mc, ItemStack removedStack, ItemStack newStack) {
        if(inv == pattern && slot == 1) {
            final ItemStack is = pattern.getInvStack(1);
            final ICraftingPatternDetails details = Api.instance().crafting().decodePattern(is, getPlayer().world, false);
            if(details != null) {
                setCraftingRecipe(details.isCraftable());
                setSubstitution(details.canSubstitute());

                for(int x = 0; x < crafting.getSlotCount() && x < details.getSparseInputs().length; x++) {
                    final IAEItemStack item = details.getSparseInputs()[x];
                    crafting.forceSetInvStack(x, item == null ? ItemStack.EMPTY : item.createItemStack());
                }

                for(int x = 0; x < output.getSlotCount() && x < details.getSparseOutputs().length; x++) {
                    final IAEItemStack item = details.getSparseOutputs()[x];
                    output.forceSetInvStack(x, item == null ? ItemStack.EMPTY : item.createItemStack());
                }
            }
        } else if(inv == crafting) fixCraftingRecipes();
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
        if(craftingMode) for(int x = 0; x < crafting.getSlotCount(); x++) {
            final ItemStack is = crafting.getInvStack(x);
            if(!is.isEmpty()) is.setCount(1);
        }
    }

    @Override
    public ItemStack getIcon() {
        return ICON;
    }
}