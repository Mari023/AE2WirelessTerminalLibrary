package de.mari_023.fabric.ae2wtlib.wet;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.implementations.blockentities.IViewCellStorage;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.stacks.GenericStack;
import appeng.crafting.pattern.AECraftingPattern;
import appeng.crafting.pattern.AEProcessingPattern;
import appeng.crafting.pattern.IAEPatternDetails;
import appeng.helpers.IPatternTerminalHost;
import appeng.menu.ISubMenu;
import appeng.parts.encoding.EncodingMode;
import appeng.util.inv.AppEngInternalInventory;
import de.mari_023.fabric.ae2wtlib.AE2wtlib;
import de.mari_023.fabric.ae2wtlib.terminal.WTMenuHost;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class WETMenuHost extends WTMenuHost implements ISegmentedInventory, IViewCellStorage, IPatternTerminalHost {

    private EncodingMode mode = EncodingMode.CRAFTING;
    private boolean substitute = false;
    private boolean substituteFluids = true;
    private final AppEngInternalInventory crafting = new AppEngInternalInventory(this, 9);
    private final AppEngInternalInventory output = new AppEngInternalInventory(this, 3);
    private final AppEngInternalInventory pattern = new AppEngInternalInventory(this, 2);

    public WETMenuHost(final Player ep, @Nullable Integer inventorySlot, final ItemStack is, BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(ep, inventorySlot, is, returnToMainMenu);
        loadFromNbt();
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(AE2wtlib.PATTERN_ENCODING_TERMINAL);
    }

    @Override
    public InternalInventory getSubInventory(ResourceLocation id) {
        if(id.equals(IPatternTerminalHost.INV_CRAFTING)) return crafting;
        else if(id.equals(IPatternTerminalHost.INV_OUTPUT)) return output;
        else if(id.equals(PATTERNS)) return pattern;
        else return null;
    }

    @Override
    public EncodingMode getMode() {
        return mode;
    }

    @Override
    public void setMode(EncodingMode mode) {
        this.mode = mode;
        fixCraftingRecipes();
    }

    private void loadFromNbt() {
        CompoundTag tag = getItemStack().getOrCreateTag();
        try {
            mode = EncodingMode.valueOf(tag.getString("wet_mode"));
        } catch(IllegalArgumentException ignored) {
            mode = EncodingMode.CRAFTING;
        }
        setSubstitution(tag.getBoolean("wet_substitute"));
        setFluidSubstitution(tag.getBoolean("wet_substituteFluids"));
        pattern.readFromNBT(tag, "wet_pattern");
        output.readFromNBT(tag, "wet_outputList");
        crafting.readFromNBT(tag, "wet_craftingGrid");
    }

    @Override
    public void saveChanges() {
        CompoundTag tag = getItemStack().getOrCreateTag();
        tag.putString("wet_mode", mode.name());
        tag.putBoolean("wet_substitute", substitute);
        tag.putBoolean("wet_substituteFluids", substituteFluids);
        pattern.writeToNBT(tag, "wet_pattern");
        output.writeToNBT(tag, "wet_outputList");
        crafting.writeToNBT(tag, "wet_craftingGrid");
    }

    @Override
    public void onChangeInventory(final InternalInventory inv, final int slot) {
        if(inv == pattern && slot == 1) {
            final ItemStack is = pattern.getStackInSlot(1);
            final IPatternDetails details = PatternDetailsHelper.decodePattern(is, getPlayer().getLevel());
            if(details instanceof AECraftingPattern) {
                setMode(EncodingMode.CRAFTING);
            } else if(details instanceof AEProcessingPattern) {
                setMode(EncodingMode.PROCESSING);
            }
            if(details instanceof IAEPatternDetails aeDetails) {
                setSubstitution(aeDetails.canSubstitute());
                setFluidSubstitution(aeDetails.canSubstituteFluids());

                for(int x = 0; x < crafting.size() && x < aeDetails.getSparseInputs().length; x++) {
                    crafting.setItemDirect(x, GenericStack.wrapInItemStack(aeDetails.getSparseInputs()[x]));
                }

                for(int x = 0; x < output.size() && x < aeDetails.getSparseOutputs().length; x++) {
                    output.setItemDirect(x, GenericStack.wrapInItemStack(aeDetails.getSparseOutputs()[x]));
                }
            }
        } else if(inv == crafting) fixCraftingRecipes();
    }

    public boolean isSubstitution() {
        return substitute;
    }

    public void setSubstitution(final boolean canSubstitute) {
        substitute = canSubstitute;
    }

    public boolean isFluidSubstitution() {
        return substituteFluids;
    }

    public void setFluidSubstitution(final boolean canSubstitute) {
        substituteFluids = canSubstitute;
    }
}