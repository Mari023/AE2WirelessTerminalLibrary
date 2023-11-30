package de.mari_023.ae2wtlib.reijei;

import java.util.Collections;
import java.util.Optional;

import net.minecraft.world.item.crafting.RecipeHolder;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.wut.recipe.Common;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;

public class WUTDisplay extends DefaultCraftingDisplay<Common> {

    public WUTDisplay(Common recipe) {// FIXME 1.20.2 probably wrong
        super(EntryIngredients.ofIngredients(recipe.getIngredients()),
                Collections.singletonList(EntryIngredients.of(recipe.getResultItem())),
                Optional.of(new RecipeHolder<>(AE2wtlib.makeID(""), recipe)));
    }

    @Override
    public int getWidth() {
        return 2;
    }

    @Override
    public int getHeight() {
        return 1;
    }
}
