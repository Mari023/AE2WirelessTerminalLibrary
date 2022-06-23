package de.mari_023.ae2wtlib.wut.recipe;

import java.util.Iterator;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.wut.ItemWUT;

import appeng.api.config.Actionable;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.items.tools.powered.powersink.AEBasePoweredItem;

public abstract class Common implements CraftingRecipe {
    /**
     * NBT keys from {@link AEBasePoweredItem}
     */
    private static final String CURRENT_POWER_NBT_KEY = "internalCurrentPower";
    private static final String MAX_POWER_NBT_KEY = "internalMaxPower";
    /**
     * NBT key used for {@link IUpgradeInventory}
     */
    private static final String TAG_UPGRADES = "upgrades";

    protected final ItemStack outputStack = new ItemStack(AE2wtlib.UNIVERSAL_TERMINAL);
    protected final ResourceLocation id;

    protected Common(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width > 1 || height > 1;
    }

    @Override
    public ItemStack getResultItem() {
        return outputStack;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    /**
     * merge a terminal into a wireless universal terminal
     *
     * @param wut          the wireless universal terminal a terminal should be added to
     * @param toMerge      the terminal to add
     * @param terminalName the name of the terminal to add
     * @return the upgraded wireless universal terminal
     */
    public ItemStack mergeTerminal(ItemStack wut, ItemStack toMerge, String terminalName) {
        if (!(wut.getItem() instanceof ItemWUT itemWUT))
            return ItemStack.EMPTY;
        if (!(toMerge.getItem() instanceof WirelessTerminalItem itemWT))
            return ItemStack.EMPTY;

        // add upgrades to nbt
        CompoundTag wutTag = wut.getOrCreateTag();
        wutTag.putBoolean(terminalName, true);
        wut.setTag(wutTag);

        // merge upgrades, this also updates max energy
        Iterator<ItemStack> iterator = itemWT.getUpgrades(toMerge).iterator();
        IUpgradeInventory wutUpgrades = itemWUT.getUpgrades(wut);
        while (iterator.hasNext()) {
            wutUpgrades.addItems(iterator.next());
        }

        // update max power
        itemWUT.onUpgradesChanged(wut, itemWUT.getUpgrades(wut));

        // merge power
        itemWUT.injectAEPower(wut, itemWT.getAECurrentPower(toMerge), Actionable.MODULATE);

        // merge nbt
        wutTag = wut.getOrCreateTag();
        CompoundTag toMergeTag = toMerge.getOrCreateTag().copy();
        // remove the keys that are already handled
        toMergeTag.remove(CURRENT_POWER_NBT_KEY);
        toMergeTag.remove(MAX_POWER_NBT_KEY);
        toMergeTag.remove(TAG_UPGRADES);
        wutTag.merge(toMergeTag);
        wut.setTag(wutTag);

        return wut;
    }
}
