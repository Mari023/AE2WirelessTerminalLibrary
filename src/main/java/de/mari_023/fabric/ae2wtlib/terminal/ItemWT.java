package de.mari_023.fabric.ae2wtlib.terminal;

import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.menu.locator.MenuLocators;
import de.mari_023.fabric.ae2wtlib.AE2wtlibConfig;
import de.mari_023.fabric.ae2wtlib.trinket.TrinketLocator;
import de.mari_023.fabric.ae2wtlib.trinket.TrinketsHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.DoubleSupplier;

public abstract class ItemWT extends WirelessTerminalItem implements IUniversalWirelessTerminalItem {

    public ItemWT(final DoubleSupplier powerCapacity, Item.Properties props) {
        super(powerCapacity, props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level w, final Player player, final InteractionHand hand) {
        var is = player.getItemInHand(hand);
        if(checkPreconditions(is, player)) {
            open(player, is, MenuLocators.forHand(player, hand));
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, is);
        }
        return new InteractionResultHolder<>(InteractionResult.FAIL, is);
    }

    @Override
    public boolean checkPreconditions(ItemStack item, Player player) {
        return super.checkPreconditions(item, player);
    }

    /**
     * Open a wireless terminal from a slot in the player inventory, i.e. activated via hotkey.
     *
     * @return True if the menu was opened.
     */
    @Override
    public boolean openFromInventory(Player player, int inventorySlot) {
        if(inventorySlot >= 100 && inventorySlot < 200 && AE2wtlibConfig.INSTANCE.allowTrinket())
            return tryOpen(player, new TrinketLocator(inventorySlot), TrinketsHelper.getTrinketsInventory(player).getStackInSlot(inventorySlot - 100));
        else
            return tryOpen(player, MenuLocators.forInventorySlot(inventorySlot), player.getInventory().getItem(inventorySlot));
    }

    /**
     * get a previously stored {@link ItemStack} from a WirelessTerminal
     *
     * @param hostItem the Terminal to load from
     * @param slot     the location where the item is stored
     * @return the stored Item or {@link ItemStack}.EMPTY if it wasn't found
     */
    public static ItemStack getSavedSlot(ItemStack hostItem, String slot) {
        if(!(hostItem.getItem() instanceof IUniversalWirelessTerminalItem)) return ItemStack.EMPTY;
        return ItemStack.of(hostItem.getOrCreateTag().getCompound(slot));
    }

    /**
     * store an {@link ItemStack} in a WirelessTerminal
     * this will overwrite any previously existing tags in slot
     *
     * @param hostItem  the Terminal to store in
     * @param savedItem the item to store
     * @param slot      the location where the stored item will be
     */
    public static void setSavedSlot(ItemStack hostItem, ItemStack savedItem, String slot) {
        if(!(hostItem.getItem() instanceof IUniversalWirelessTerminalItem)) return;
        CompoundTag wctTag = hostItem.getOrCreateTag();
        if(savedItem.isEmpty()) wctTag.remove(slot);
        else wctTag.put(slot, savedItem.save(new CompoundTag()));
    }

    /**
     * get a previously stored boolean from a WirelessTerminal
     *
     * @param hostItem the Terminal to load from
     * @return the boolean or false if it wasn't found
     */
    public static boolean getBoolean(ItemStack hostItem, String key) {
        if(!(hostItem.getItem() instanceof IUniversalWirelessTerminalItem)) return false;
        return hostItem.getOrCreateTag().getBoolean(key);
    }

    /**
     * store a boolean in a WirelessTerminal
     * this will overwrite any previously existing tags in slot
     *
     * @param hostItem the Terminal to store in
     * @param b        the boolean to store
     * @param key      the location where the stored item will be
     */
    public static void setBoolean(ItemStack hostItem, boolean b, String key) {
        if(!(hostItem.getItem() instanceof IUniversalWirelessTerminalItem)) return;
        CompoundTag wctTag = hostItem.getOrCreateTag();
        wctTag.putBoolean(key, b);
    }
}