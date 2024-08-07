package de.mari_023.ae2wtlib.wut.recipe;

import java.util.Iterator;

import com.mojang.datafixers.util.Unit;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;

import appeng.api.config.Actionable;
import appeng.api.ids.AEComponents;
import appeng.api.upgrades.IUpgradeInventory;

import de.mari_023.ae2wtlib.AE2wtlibItems;
import de.mari_023.ae2wtlib.api.registration.WTDefinition;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import de.mari_023.ae2wtlib.api.terminal.ItemWUT;

public abstract class Common implements CraftingRecipe {
    protected final ItemStack outputStack = new ItemStack(AE2wtlibItems.UNIVERSAL_TERMINAL);

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width > 1 || height > 1;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return outputStack;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.EQUIPMENT;
    }

    /**
     * merge a terminal into a wireless universal terminal
     *
     * @param wut      the wireless universal terminal a terminal should be added to
     * @param toMerge  the terminal to add
     * @param terminal the terminal to add
     * @return the upgraded wireless universal terminal
     */
    public static ItemStack mergeTerminal(ItemStack wut, ItemStack toMerge, WTDefinition terminal) {
        if (!(wut.getItem() instanceof ItemWUT itemWUT))
            return ItemStack.EMPTY;
        if (!(toMerge.getItem() instanceof ItemWT itemWT))
            return ItemStack.EMPTY;

        wut.set(terminal.componentType(), Unit.INSTANCE);

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
        var componentsPatch = toMerge.getComponentsPatch();
        // remove the keys that are already handled
        componentsPatch = componentsPatch.forget(AEComponents.STORED_ENERGY::equals);
        componentsPatch = componentsPatch.forget(AEComponents.ENERGY_CAPACITY::equals);
        componentsPatch = componentsPatch.forget(AEComponents.UPGRADES::equals);
        wut.applyComponents(componentsPatch);

        return wut;
    }
}
