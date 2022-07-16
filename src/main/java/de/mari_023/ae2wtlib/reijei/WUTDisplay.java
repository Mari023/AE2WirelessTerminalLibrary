package de.mari_023.ae2wtlib.reijei;

import java.util.Collections;
import java.util.Optional;

import de.mari_023.ae2wtlib.wut.recipe.Common;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;

public class WUTDisplay extends DefaultCraftingDisplay<Common> {

    public WUTDisplay(Common recipe) {
        super(EntryIngredients.ofIngredients(recipe.getIngredients()),
                Collections.singletonList(EntryIngredients.of(recipe.getResultItem())), Optional.of(recipe));
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
