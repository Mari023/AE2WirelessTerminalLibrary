package de.mari_023.ae2wtlib.terminal;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.wut.WUTHandler;

import appeng.api.config.Settings;
import appeng.api.config.ShowPatternProviders;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.util.IConfigManager;
import appeng.core.AEConfig;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.menu.locator.MenuLocators;

public abstract class ItemWT extends WirelessTerminalItem implements IUniversalWirelessTerminalItem {

    public ItemWT() {
        super(AEConfig.instance().getWirelessTerminalBattery(),
                new Item.Properties().tab(AE2wtlib.ITEM_GROUP).stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level w, final Player player, final InteractionHand hand) {
        ItemStack is = player.getItemInHand(hand);
        if (checkUniversalPreconditions(is, player)) {
            open(player, is, MenuLocators.forHand(player, hand));
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, is);
        }
        return new InteractionResultHolder<>(InteractionResult.FAIL, is);
    }

    @Override
    public boolean openFromInventory(Player player, int inventorySlot) {// TODO maybe remove this since it shouldn't get
        // called anyways
        throw new IllegalStateException(
                String.format("%s called openFromInventory() in slot %d with item %s", player, inventorySlot, this));
    }

    @Nullable
    public ItemMenuHost getMenuHost(Player player, int slot, ItemStack stack, @Nullable BlockPos pos) {
        return WUTHandler.wirelessTerminals.get(WUTHandler.getCurrentTerminal(stack)).wTMenuHostFactory().create(player,
                slot, stack, (p, subMenu) -> tryOpen(player, MenuLocators.forInventorySlot(slot), stack));
    }

    /**
     * get a previously stored {@link ItemStack} from a WirelessTerminal
     *
     * @param hostItem the Terminal to load from
     * @param slot     the location where the item is stored
     * @return the stored Item or {@link ItemStack}.EMPTY if it wasn't found
     */
    @Deprecated
    public static ItemStack getSavedSlot(ItemStack hostItem, String slot) {
        if (!(hostItem.getItem() instanceof IUniversalWirelessTerminalItem))
            return ItemStack.EMPTY;
        return ItemStack.of(hostItem.getOrCreateTag().getCompound(slot));
    }

    /**
     * store an {@link ItemStack} in a WirelessTerminal this will overwrite any previously existing tags in slot
     *
     * @param hostItem  the Terminal to store in
     * @param savedItem the item to store
     * @param slot      the location where the stored item will be
     */
    @Deprecated
    public static void setSavedSlot(ItemStack hostItem, ItemStack savedItem, String slot) {
        if (!(hostItem.getItem() instanceof IUniversalWirelessTerminalItem))
            return;
        CompoundTag wctTag = hostItem.getOrCreateTag();
        if (savedItem.isEmpty())
            wctTag.remove(slot);
        else
            wctTag.put(slot, savedItem.save(new CompoundTag()));
    }

    /**
     * get a previously stored boolean from a WirelessTerminal
     *
     * @param hostItem the Terminal to load from
     * @return the boolean or false if it wasn't found
     */
    public static boolean getBoolean(ItemStack hostItem, String key) {
        if (!(hostItem.getItem() instanceof IUniversalWirelessTerminalItem))
            return false;
        return hostItem.getOrCreateTag().getBoolean(key);
    }

    /**
     * store a boolean in a WirelessTerminal this will overwrite any previously existing tags in slot
     *
     * @param hostItem the Terminal to store in
     * @param b        the boolean to store
     * @param key      the location where the stored item will be
     */
    public static void setBoolean(ItemStack hostItem, boolean b, String key) {
        if (!(hostItem.getItem() instanceof IUniversalWirelessTerminalItem))
            return;
        CompoundTag wctTag = hostItem.getOrCreateTag();
        wctTag.putBoolean(key, b);
    }

    /**
     * add config for wat (also needed for wut)
     */
    public IConfigManager getConfigManager(ItemStack target) {
        var configManager = super.getConfigManager(target);
        if (WUTHandler.getCurrentTerminal(target).equals("pattern_access")) {
            configManager.registerSetting(Settings.TERMINAL_SHOW_PATTERN_PROVIDERS, ShowPatternProviders.VISIBLE);
            configManager.readFromNBT(target.getOrCreateTag().copy());
        }
        return configManager;
    }
}
