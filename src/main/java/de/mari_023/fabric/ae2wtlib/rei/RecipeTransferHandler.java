package de.mari_023.fabric.ae2wtlib.rei;

import appeng.helpers.IContainerCraftingPacket;
import me.shedaniel.rei.api.AutoTransferHandler;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import me.shedaniel.rei.api.TransferRecipeDisplay;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;
import java.util.stream.Stream;

abstract class RecipeTransferHandler<T extends ScreenHandler & IContainerCraftingPacket> implements AutoTransferHandler {

    private final Class<T> containerClass;

    RecipeTransferHandler(Class<T> containerClass) {
        this.containerClass = containerClass;
    }

    @Override
    public AutoTransferHandler.Result handle(AutoTransferHandler.Context context) {
        RecipeDisplay recipe = context.getRecipe();

        if (!containerClass.isInstance(context.getContainerScreen().getScreenHandler())) {
            return AutoTransferHandler.Result.createNotApplicable();
        }

        T container = containerClass.cast(context.getContainerScreen().getScreenHandler());

        final Identifier recipeId = recipe.getRecipeLocation().orElse(null);

        // Check that the recipe can actually be looked up via the manager, i.e. our
        // facade recipes
        // have an ID, but are never registered with the recipe manager.
        boolean canSendReference = recipeId != null && context.getMinecraft().world.getRecipeManager().get(recipeId).isPresent();

        if (recipe instanceof TransferRecipeDisplay) {
            TransferRecipeDisplay trd = (TransferRecipeDisplay) recipe;
            if (trd.getWidth() > 3 || trd.getHeight() > 3) {
                return AutoTransferHandler.Result.createFailed("jei.appliedenergistics2.recipe_too_large");
            }
        } else if (recipe.getInputEntries().size() > 9) {
            return AutoTransferHandler.Result.createFailed("jei.appliedenergistics2.recipe_too_large");
        }

        final AutoTransferHandler.Result error = doTransferRecipe(container, recipe, context);

        if (error != null) {
            return error;
        }

        if (context.isActuallyCrafting()) {
            if (canSendReference) {
                new REIRecipePacket(recipeId, isCrafting()).send();
            } else {
                // To avoid earlier problems of too large packets being sent that crashed the
                // client,
                // as a fallback when the recipe ID could not be resolved, we'll just send the
                // displayed
                // items.
                DefaultedList<Ingredient> flatIngredients = DefaultedList.ofSize(9, Ingredient.EMPTY);
                ItemStack output = null;
                for (EntryStack entryStack : recipe.getResultingEntries().get(0)) {
                    if (entryStack.getType() == EntryStack.Type.ITEM) {
                        output = entryStack.getItemStack();
                    }
                }
                if (output == null || output.isEmpty()) {
                    return AutoTransferHandler.Result.createFailed("jei.appliedenergistics2.no_output");
                }

                // Now map the actual ingredients into the output/input
                for (int i = 0; i < recipe.getInputEntries().size(); i++) {
                    List<EntryStack> inputEntry = recipe.getInputEntries().get(i);
                    if (inputEntry.isEmpty()) {
                        continue;
                    }
                    EntryStack first = inputEntry.get(0);
                    if (i < flatIngredients.size()) {
                        ItemStack displayedIngredient = first.getItemStack();
                        if (displayedIngredient != null) {
                            flatIngredients.set(i, Ingredient.ofStacks(Stream.of(displayedIngredient)));
                        }
                    }
                }

                new REIRecipePacket(new ShapedRecipe(recipeId, "", 3, 3, flatIngredients, output), isCrafting()).send();
            }
        }

        return Result.createSuccessful().blocksFurtherHandling();
    }

    protected abstract AutoTransferHandler.Result doTransferRecipe(T container, RecipeDisplay recipe, AutoTransferHandler.Context context);

    protected abstract boolean isCrafting();
}