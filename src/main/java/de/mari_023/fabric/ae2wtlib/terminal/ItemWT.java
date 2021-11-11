package de.mari_023.fabric.ae2wtlib.terminal;

import appeng.api.features.Locatables;
import appeng.api.networking.security.IActionHost;
import appeng.core.AEConfig;
import appeng.core.localization.PlayerMessages;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.menu.MenuLocator;
import appeng.util.Platform;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.world.World;

import java.util.OptionalLong;

public abstract class ItemWT extends WirelessTerminalItem {

    public ItemWT(Item.Settings props) {
        super(AEConfig.instance().getWirelessTerminalBattery(), props);
    }

    @Override
    public TypedActionResult<ItemStack> use(final World w, final PlayerEntity player, final Hand hand) {
        if(canOpen(player.getStackInHand(hand), player)) open(player, MenuLocator.forHand(player, hand));
        return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
    }

    public boolean canOpen(ItemStack item, PlayerEntity player) {//TODO use WirelessTerminalsInternal#checkPreconditions()
        if(Platform.isClient()) return false;

        final OptionalLong unparsedKey = getGridKey(item);
        if(unparsedKey.isEmpty()) {
            player.sendSystemMessage(PlayerMessages.DeviceNotLinked.get(), Util.NIL_UUID);
            return false;
        }

        final long parsedKey = unparsedKey.getAsLong();
        final IActionHost securityStation = Locatables.securityStations().get(player.world, parsedKey);
        if(securityStation == null) {
            player.sendSystemMessage(PlayerMessages.StationCanNotBeLocated.get(), Util.NIL_UUID);
            return false;
        }
        if(TERMINAL_HANDLER.hasPower(player, 0.5, item)) return true;
        else {
            player.sendSystemMessage(PlayerMessages.DeviceNotPowered.get(), Util.NIL_UUID);
            return false;
        }
    }

    public void tryOpen(PlayerEntity player, MenuLocator locator, ItemStack stack) {
        if(canOpen(stack, player)) open(player, locator);
    }

    public abstract void open(final PlayerEntity player, final MenuLocator locator);

    /**
     * get a previously stored {@link ItemStack} from a WirelessTerminal
     *
     * @param hostItem the Terminal to load from
     * @param slot     the location where the item is stored
     * @return the stored Item or {@link ItemStack}.EMPTY if it wasn't found
     */
    public static ItemStack getSavedSlot(ItemStack hostItem, String slot) {
        if(!(hostItem.getItem() instanceof ItemWT)) return ItemStack.EMPTY;
        return ItemStack.fromNbt(hostItem.getOrCreateNbt().getCompound(slot));
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
        if(!(hostItem.getItem() instanceof ItemWT)) return;
        NbtCompound wctTag = hostItem.getOrCreateNbt();
        if(savedItem.isEmpty()) wctTag.remove(slot);
        else wctTag.put(slot, savedItem.writeNbt(new NbtCompound()));
    }

    /**
     * get a previously stored boolean from a WirelessTerminal
     *
     * @param hostItem the Terminal to load from
     * @return the boolean or false if it wasn't found
     */
    public static boolean getBoolean(ItemStack hostItem, String key) {
        if(!(hostItem.getItem() instanceof ItemWT)) return false;
        return hostItem.getOrCreateNbt().getBoolean(key);
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
        if(!(hostItem.getItem() instanceof ItemWT)) return;
        NbtCompound wctTag = hostItem.getOrCreateNbt();
        wctTag.putBoolean(key, b);
    }

    public boolean hasBoosterCard(ItemStack hostItem) {
        return getBoosterCard(hostItem).getItem() instanceof ItemInfinityBooster;
    }

    public void setBoosterCard(ItemStack hostItem, ItemStack boosterCard) {
        if(hostItem.getItem() instanceof IInfinityBoosterCardHolder) setSavedSlot(hostItem, boosterCard, "boosterCard");
    }

    public ItemStack getBoosterCard(ItemStack hostItem) {
        if(hostItem.getItem() instanceof IInfinityBoosterCardHolder) return getSavedSlot(hostItem, "boosterCard");
        return ItemStack.EMPTY;
    }
}