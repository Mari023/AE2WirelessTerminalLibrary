package de.mari_023.fabric.ae2wtlib.rei;

import alexiil.mc.lib.attributes.item.FixedItemInv;
import appeng.api.config.Actionable;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.security.ISecurityService;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.storage.IStorageService;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.StorageChannels;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.core.Api;
import appeng.helpers.IMenuCraftingPacket;
import appeng.items.storage.ViewCellItem;
import appeng.me.service.SecurityService;
import appeng.mixins.IngredientAccessor;
import appeng.util.Platform;
import appeng.util.helpers.ItemHandlerUtil;
import appeng.util.inv.AdaptorFixedInv;
import appeng.util.inv.WrapperInvItemHandler;
import appeng.util.item.AEItemStack;
import appeng.util.prioritylist.IPartitionList;
import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Pair;
import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import de.mari_023.fabric.ae2wtlib.wpt.WPTContainer;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.Arrays;
import java.util.stream.Stream;

public class REIRecipePacket {

    /**
     * Transmit only a recipe ID.
     */
    private static final int INLINE_RECIPE_NONE = 1;

    /**
     * Transmit the information about the recipe we actually need. This is explicitly limited since this is untrusted
     * client->server info.
     */
    private static final int INLINE_RECIPE_SHAPED = 2;

    private Identifier recipeId;
    /**
     * This is optional, in case the client already knows it could not resolve the recipe id.
     */
    private Recipe<?> recipe;
    private final boolean crafting;

    private final PacketByteBuf data;

    public REIRecipePacket(final Identifier recipeId, final boolean crafting) {
        data = createCommonHeader(recipeId, crafting, INLINE_RECIPE_NONE);
        this.crafting = crafting;
        this.recipeId = recipeId;
    }

    public REIRecipePacket(final ShapedRecipe recipe, final boolean crafting) {
        data = createCommonHeader(recipe.getId(), crafting, INLINE_RECIPE_SHAPED);
        RecipeSerializer.SHAPED.write(data, recipe);
        this.crafting = crafting;
    }

    private PacketByteBuf createCommonHeader(Identifier recipeId, boolean crafting, int inlineRecipeType) {
        final PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());

        data.writeBoolean(crafting);
        data.writeIdentifier(recipeId);
        data.writeVarInt(inlineRecipeType);

        return data;
    }

    public void send() {
        ClientPlayNetworking.send(new Identifier(ae2wtlib.MOD_NAME, "rei_recipe"), data);
    }

    /**
     * Incoming Packets
     *
     * @param buf    the packets buffer, needs to be retained and released
     * @param player the player who sent the package
     */
    public REIRecipePacket(PacketByteBuf buf, ServerPlayerEntity player) {
        data = buf;
        crafting = data.readBoolean();
        final String id = data.readString(Short.MAX_VALUE);
        recipeId = new Identifier(id);

        int inlineRecipeType = data.readVarInt();
        switch(inlineRecipeType) {
            case INLINE_RECIPE_SHAPED:
                recipe = RecipeSerializer.SHAPED.read(recipeId, data);
            case INLINE_RECIPE_NONE:
                break;
            default:
                throw new IllegalArgumentException("Invalid inline recipe type.");
        }
        final ScreenHandler con = player.currentScreenHandler;
        Preconditions.checkArgument(con instanceof IMenuCraftingPacket);

        Recipe<?> recipe = player.getEntityWorld().getRecipeManager().get(recipeId).orElse(null);
        if(recipe == null && this.recipe != null) {
            // Certain recipes (i.e. AE2 facades) are represented in JEI as ShapedRecipe's, while in reality they are special recipes. Those recipes are sent across the wire...
            recipe = this.recipe;
        }
        Preconditions.checkArgument(recipe != null);

        final IMenuCraftingPacket cct = (IMenuCraftingPacket) con;
        final IGridNode node = cct.getNetworkNode();

        Preconditions.checkArgument(node != null);

        final IGrid grid = node.getGrid();

        final ISecurityService security = grid.getSecurityService();
        final IEnergyService energy = grid.getEnergyService();
        final FixedItemInv craftMatrix = cct.getSubInventory("crafting");

        final IMEMonitor<IAEItemStack> storage = grid.getStorageService().getInventory(StorageChannels.items());
        final IPartitionList<IAEItemStack> filter = ViewCellItem.createFilter(cct.getViewCells());

        // Handle each slot
        for(int x = 0; x < craftMatrix.getSlotCount(); x++) {
            ItemStack currentItem = craftMatrix.getInvStack(x);
            Ingredient ingredient = ensure3by3CraftingMatrix(recipe).get(x);

            // prepare slots
            if(!currentItem.isEmpty()) {
                // already the correct item? True, skip everything else
                ItemStack newItem = canUseInSlot(ingredient, currentItem);

                // put away old item, if not correct
                if(newItem != currentItem && security.hasPermission(player, SecurityPermissions.INJECT)) {
                    final IAEItemStack in = AEItemStack.fromItemStack(currentItem);
                    final IAEItemStack out = cct.useRealItems() ? Platform.poweredInsert(energy, storage, in, cct.getActionSource()) : null;
                    if(out != null) {
                        currentItem = out.createItemStack();
                    } else {
                        currentItem = ItemStack.EMPTY;
                    }
                }
            }

            // Find item or pattern from the network
            if(currentItem.isEmpty() && security.hasPermission(player, SecurityPermissions.EXTRACT)) {
                IAEItemStack out;

                if(cct.useRealItems()) {
                    IAEItemStack request = findBestMatchingItemStack(ingredient, filter, storage, cct);
                    out = request != null ? Platform.poweredExtraction(energy, storage, request.setStackSize(1), cct.getActionSource()) : null;
                } else {
                    out = findBestMatchingPattern(ingredient, filter, grid.getCache(ICraftingGrid.class), storage, cct);
                    if(out == null) {
                        out = findBestMatchingItemStack(ingredient, filter, storage, cct);
                    }
                    if(out == null && getMatchingStacks(ingredient).length > 0) {
                        out = AEItemStack.fromItemStack(getMatchingStacks(ingredient)[0]);
                    }
                }

                if(out != null) {
                    currentItem = out.createItemStack();
                }
            }

            // If still nothing, search the player inventory.
            if(currentItem.isEmpty()) {
                ItemStack[] matchingStacks = getMatchingStacks(ingredient);
                for(ItemStack matchingStack : matchingStacks) {
                    if(currentItem.isEmpty()) {
                        AdaptorFixedInv ad = new AdaptorFixedInv(cct.getInventoryByName("player"));

                        if(cct.useRealItems()) {
                            currentItem = ad.removeItems(1, matchingStack, null);
                        } else {
                            currentItem = ad.simulateRemove(1, matchingStack, null);
                        }
                    }
                }
            }
            ItemHandlerUtil.setStackInSlot(craftMatrix, x, currentItem);
        }

        if(!this.crafting) {
            handleProcessing(con, cct, recipe);
        }

        con.onContentChanged(new WrapperInvItemHandler(craftMatrix));
    }

    private static ItemStack[] getMatchingStacks(Ingredient ingredient) {
        IngredientAccessor accessor = (IngredientAccessor) (Object) ingredient;
        accessor.appeng_cacheMatchingStacks();
        if(ingredient.isEmpty()) {
            return new ItemStack[0];
        }
        ItemStack[] stacks = accessor.appeng_getMatchingStacks();
        if(stacks.length == 1 && stacks[0].isEmpty()) {
            return new ItemStack[0];
        }
        return stacks;
    }

    /**
     * Expand any recipe to a 3x3 matrix.
     * <p>
     * Will throw an {@link IllegalArgumentException} in case it has more than 9 or a shaped recipe is either wider or
     * higher than 3. ingredients.
     */
    private DefaultedList<Ingredient> ensure3by3CraftingMatrix(Recipe<?> recipe) {
        DefaultedList<Ingredient> ingredients = recipe.getIngredients();
        DefaultedList<Ingredient> expandedIngredients = DefaultedList.ofSize(9, Ingredient.EMPTY);

        Preconditions.checkArgument(ingredients.size() <= 9);

        // shaped recipes can be smaller than 3x3, expand to 3x3 to match the crafting matrix
        if(recipe instanceof ShapedRecipe) {
            ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;
            int width = shapedRecipe.getWidth();
            int height = shapedRecipe.getHeight();
            Preconditions.checkArgument(width <= 3 && height <= 3);

            for(int h = 0; h < height; h++) {
                for(int w = 0; w < width; w++) {
                    int source = w + h * width;
                    int target = w + h * 3;
                    Ingredient i = ingredients.get(source);
                    expandedIngredients.set(target, i);
                }
            }
        }
        // Anything else should be a flat list
        else {
            for(int i = 0; i < ingredients.size(); i++) {
                expandedIngredients.set(i, ingredients.get(i));
            }
        }

        return expandedIngredients;
    }

    /**
     * @param is itemstack
     * @return is if it can be used, else EMPTY
     */
    private ItemStack canUseInSlot(Ingredient ingredient, ItemStack is) {
        return Arrays.stream(getMatchingStacks(ingredient)).filter(p -> p.isItemEqual(is)).findFirst().orElse(ItemStack.EMPTY);
    }

    /**
     * Finds the first matching itemstack with the highest stored amount.
     */
    private IAEItemStack findBestMatchingItemStack(Ingredient ingredients, IPartitionList<IAEItemStack> filter, IMEMonitor<IAEItemStack> storage, IMenuCraftingPacket cct) {
        return getMostStored(Arrays.stream(getMatchingStacks(ingredients)).map(AEItemStack::fromItemStack).filter(r -> r != null && (filter == null || filter.isListed(r))), storage, cct);
    }

    /**
     * This tries to find the first pattern matching the list of ingredients.
     * <p>
     * As additional condition, it sorts by the stored amount to return the one with the highest stored amount.
     */
    private IAEItemStack findBestMatchingPattern(Ingredient ingredients, IPartitionList<IAEItemStack> filter, ICraftingGrid crafting, IMEMonitor<IAEItemStack> storage, IMenuCraftingPacket cct) {
        return getMostStored(Arrays.stream(getMatchingStacks(ingredients)).map(AEItemStack::fromItemStack).filter(r -> r != null && (filter == null || filter.isListed(r))).map(s -> s.setCraftable(!crafting.getCraftingFor(s, null, 0, null).isEmpty())).filter(IAEItemStack::isCraftable), storage, cct);
    }

    /**
     * From a stream of AE item stacks, pick the one with the highest available amount in the network. Returns null if the stream is empty.
     */
    private static IAEItemStack getMostStored(Stream<? extends IAEItemStack> stacks, IMEMonitor<IAEItemStack> storage, IMenuCraftingPacket cct) {
        return stacks.map(s -> {
            // Determine the stored count
            IAEItemStack stored = storage.extractItems(s.copy().setStackSize(Long.MAX_VALUE),
                    Actionable.SIMULATE, cct.getActionSource());
            return Pair.of(s, stored != null ? stored.getStackSize() : 0);
        }).min((left, right) -> Long.compare(right.getSecond(), left.getSecond())).map(Pair::getFirst).orElse(null);
    }

    private void handleProcessing(ScreenHandler con, IMenuCraftingPacket cct, Recipe<?> recipe) {
        if(con instanceof WPTContainer && !((WPTContainer) con).craftingMode) {
            final FixedItemInv output = cct.getSubInventory("output");
            ItemHandlerUtil.setStackInSlot(output, 0, recipe.getOutput());
            ItemHandlerUtil.setStackInSlot(output, 1, ItemStack.EMPTY);
            ItemHandlerUtil.setStackInSlot(output, 2, ItemStack.EMPTY);
        }
    }
}