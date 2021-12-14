package de.mari_023.fabric.ae2wtlib.wut.recipe;

import de.mari_023.fabric.ae2wtlib.AE2wtlib;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;

public abstract class Common implements CraftingRecipe {

    protected final ItemStack outputStack = new ItemStack(AE2wtlib.UNIVERSAL_TERMINAL);
    protected final ResourceLocation id;

    protected Common(ResourceLocation id) {
        this.id = id;
    }

    @Environment(EnvType.CLIENT)
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
}