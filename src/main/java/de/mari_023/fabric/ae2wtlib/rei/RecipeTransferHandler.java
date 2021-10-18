package de.mari_023.fabric.ae2wtlib.rei;

import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.JEIRecipePacket;
import appeng.helpers.IMenuCraftingPacket;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandler;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;

abstract class RecipeTransferHandler<T extends ScreenHandler & IMenuCraftingPacket> implements TransferHandler {

    private final Class<T> containerClass;

    RecipeTransferHandler(Class<T> containerClass) {
        this.containerClass = containerClass;
    }

    @Override
    public Result handle(Context context) {
        if(!containerClass.isInstance(context.getMenu())) {
            return Result.createNotApplicable();
        }

        Display recipe = context.getDisplay();

        T menu = containerClass.cast(context.getMenu());

        var recipeId = recipe.getDisplayLocation().orElse(null);

        // Check that the recipe can actually be looked up via the manager, i.e. our
        // facade recipes
        // have an ID, but are never registered with the recipe manager.
        boolean canSendReference = recipeId != null && context.getMinecraft().world != null && context.getMinecraft().world.getRecipeManager().get(recipeId).isPresent();

        if(recipe instanceof SimpleGridMenuDisplay gridDisplay) {
            if(gridDisplay.getWidth() > 3 || gridDisplay.getHeight() > 3) {
                return Result.createFailed(new TranslatableText("jei.appliedenergistics2.recipe_too_large"));
            }
        } else if(recipe.getInputEntries().size() > 9) {
            return Result.createFailed(new TranslatableText("jei.appliedenergistics2.recipe_too_large"));
        }

        final Result error = doTransferRecipe(menu, recipe);

        if(error != null) {
            return error;
        }

        if(context.isActuallyCrafting()) {
            if(canSendReference) {
                NetworkHandler.instance().sendToServer(new JEIRecipePacket(recipeId));
            } else {
                // To avoid earlier problems of too large packets being sent that crashed the
                // client,
                // as a fallback when the recipe ID could not be resolved, we'll just send the
                // displayed
                // items.
                DefaultedList<Ingredient> flatIngredients = DefaultedList.ofSize(9, Ingredient.EMPTY);
                ItemStack output = null;
                for(EntryStack<?> entryStack : recipe.getOutputEntries().get(0)) {
                    if(entryStack.getType() == VanillaEntryTypes.ITEM) {
                        output = entryStack.castValue();
                    }
                }
                if(output == null || output.isEmpty()) {
                    return Result.createFailed(new TranslatableText("jei.appliedenergistics2.no_output"));
                }

                // Now map the actual ingredients into the output/input
                for(int i = 0; i < recipe.getInputEntries().size(); i++) {
                    var inputIngredient = recipe.getInputEntries().get(i);
                    if(inputIngredient.isEmpty()) {
                        continue;
                    }
                    if(i < flatIngredients.size()) {
                        var ingredients = inputIngredient
                                .stream()
                                .filter(entry -> entry.getType() == VanillaEntryTypes.ITEM)
                                .map(entry -> (ItemStack) entry.getValue());
                        flatIngredients.set(i, Ingredient.ofStacks(ingredients));
                    }
                }

                ShapedRecipe fallbackRecipe = new ShapedRecipe(recipeId, "", 3, 3, flatIngredients, output);
                NetworkHandler.instance().sendToServer(new JEIRecipePacket(fallbackRecipe));
            }
        }

        return Result.createSuccessful().blocksFurtherHandling();
    }

    protected abstract Result doTransferRecipe(T container, Display recipe);
}