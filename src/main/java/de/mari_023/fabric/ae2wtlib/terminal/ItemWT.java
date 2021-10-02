package de.mari_023.fabric.ae2wtlib.terminal;

import appeng.api.config.Actionable;
import appeng.api.config.SortDir;
import appeng.api.config.SortOrder;
import appeng.api.config.ViewItems;
import appeng.api.features.IWirelessTerminalHandler;
import appeng.api.features.Locatables;
import appeng.api.networking.security.IActionHost;
import appeng.api.util.IConfigManager;
import appeng.core.localization.GuiText;
import appeng.core.localization.PlayerMessages;
import appeng.hooks.ICustomReequipAnimation;
import appeng.items.tools.powered.powersink.AEBasePoweredItem;
import appeng.menu.MenuLocator;
import appeng.util.ConfigManager;
import appeng.util.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.world.World;

import java.util.List;
import java.util.OptionalLong;

public abstract class ItemWT extends AEBasePoweredItem implements IWirelessTerminalHandler, ICustomReequipAnimation {

    public ItemWT(Item.Settings props) {
        super(/*AEConfig.instance().getWirelessTerminalBattery()*/ () -> 1000000, props);
    }

    @Override
    public TypedActionResult<ItemStack> use(final World w, final PlayerEntity player, final Hand hand) {
        if(canOpen(player.getStackInHand(hand), player)) open(player, MenuLocator.forHand(player, hand));
        return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
    }

    public boolean canOpen(ItemStack item, PlayerEntity player) {
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
        if(hasPower(player, 0.5, item)) return true;
        else {
            player.sendSystemMessage(PlayerMessages.DeviceNotPowered.get(), Util.NIL_UUID);
            return false;
        }
    }

    public void tryOpen(PlayerEntity player, MenuLocator locator, ItemStack stack) {
        if(canOpen(stack, player)) open(player, locator);
    }

    public abstract void open(final PlayerEntity player, final MenuLocator locator);

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(final ItemStack stack, final World world, final List<Text> lines, final TooltipContext advancedTooltips) {
        super.appendTooltip(stack, world, lines, advancedTooltips);

        if(stack.hasNbt()) {
            final NbtCompound tag = stack.getOrCreateNbt();
            if(tag != null) {
                final String encKey = tag.getString("encryptionKey");

                if(encKey == null || encKey.isEmpty()) lines.add(GuiText.Unlinked.text());
                else lines.add(GuiText.Linked.text());
            }
        } else lines.add(new TranslatableText("AppEng.GuiITooltip.Unlinked"));
    }

    @Override
    public boolean usePower(PlayerEntity player, double amount, ItemStack is) {
        return extractAEPower(is, amount, Actionable.MODULATE) >= amount - 0.5;
    }

    @Override
    public boolean hasPower(PlayerEntity player, double amount, ItemStack is) {
        return getAECurrentPower(is) >= amount;
    }

    @Override
    public IConfigManager getConfigManager(ItemStack is) {
        ConfigManager out = new ConfigManager((manager, settingName) -> manager.writeToNBT(is.getOrCreateNbt()));

        out.registerSetting(appeng.api.config.Settings.SORT_BY, SortOrder.NAME);
        out.registerSetting(appeng.api.config.Settings.VIEW_MODE, ViewItems.ALL);
        out.registerSetting(appeng.api.config.Settings.SORT_DIRECTION, SortDir.ASCENDING);

        out.readFromNBT(is.getOrCreateNbt().copy());
        return out;
    }

    @Override
    public OptionalLong getGridKey(ItemStack item) {
        NbtCompound tag = item.getNbt();
        return tag != null && tag.contains("gridKey", 4) ? OptionalLong.of(tag.getLong("gridKey")) : OptionalLong.empty();
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

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