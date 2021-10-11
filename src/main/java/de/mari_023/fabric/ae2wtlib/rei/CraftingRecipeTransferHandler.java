package de.mari_023.fabric.ae2wtlib.rei;

import de.mari_023.fabric.ae2wtlib.wct.WCTContainer;
import me.shedaniel.rei.api.common.display.Display;

public class CraftingRecipeTransferHandler extends RecipeTransferHandler<WCTContainer> {

    public CraftingRecipeTransferHandler() {
        super(WCTContainer.class);
    }


    @Override
    protected Result doTransferRecipe(WCTContainer container, Display recipe, Context context) {
        return null;
    }

    @Override
    protected boolean isCrafting() {
        return true;
    }
}