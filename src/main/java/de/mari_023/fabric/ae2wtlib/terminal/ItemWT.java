package de.mari_023.fabric.ae2wtlib.terminal;

import appeng.api.config.Actionable;
import appeng.api.config.SortDir;
import appeng.api.config.SortOrder;
import appeng.api.config.ViewItems;
import appeng.api.features.ILocatable;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.util.IConfigManager;
import appeng.core.Api;
import appeng.core.localization.GuiText;
import appeng.core.localization.PlayerMessages;
import appeng.items.tools.powered.powersink.AEBasePoweredItem;
import appeng.util.ConfigManager;
import appeng.util.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.DoubleSupplier;

public abstract class ItemWT extends AEBasePoweredItem implements IWirelessTermHandler {

    public ItemWT(DoubleSupplier powerCapacity, Settings props) {
        super(powerCapacity, props);
    }

    @Override
    public TypedActionResult<ItemStack> use(final World w, final PlayerEntity player, final Hand hand) {
        openWirelessTerminalGui(player.getStackInHand(hand), player, hand);
        return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
    }

    private void openWirelessTerminalGui(ItemStack item, PlayerEntity player, Hand hand) {
        if(Platform.isClient()) {
            return;
        }

        final String unparsedKey = getEncryptionKey(item);
        if(unparsedKey.isEmpty()) {
            player.sendSystemMessage(PlayerMessages.DeviceNotLinked.get(), Util.NIL_UUID);
            return;
        }

        final long parsedKey = Long.parseLong(unparsedKey);
        final ILocatable securityStation = Api.instance().registries().locatable().getLocatableBy(parsedKey);
        if(securityStation == null) {
            player.sendSystemMessage(PlayerMessages.StationCanNotBeLocated.get(), Util.NIL_UUID);
            return;
        }

        if(hasPower(player, 0.5, item)) {
            open(player, hand);
        } else {
            player.sendSystemMessage(PlayerMessages.DeviceNotPowered.get(), Util.NIL_UUID);
        }
    }

    public abstract void open(final PlayerEntity player, final Hand hand);

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(final ItemStack stack, final World world, final List<Text> lines, final TooltipContext advancedTooltips) {
        super.appendTooltip(stack, world, lines, advancedTooltips);

        if(stack.hasTag()) {
            final CompoundTag tag = stack.getOrCreateTag();
            if(tag != null) {
                final String encKey = tag.getString("encryptionKey");

                if(encKey == null || encKey.isEmpty()) {
                    lines.add(GuiText.Unlinked.text());
                } else {
                    lines.add(GuiText.Linked.text());
                }
            }
        } else {
            lines.add(new TranslatableText("AppEng.GuiITooltip.Unlinked"));
        }
    }

    @Override
    public boolean canHandle(ItemStack is) {
        return is.getItem() instanceof ItemWT;
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
        final ConfigManager out = new ConfigManager((manager, settingName, newValue) -> {
            final CompoundTag data = is.getOrCreateTag();
            manager.writeToNBT(data);
        });

        out.registerSetting(appeng.api.config.Settings.SORT_BY, SortOrder.NAME);
        out.registerSetting(appeng.api.config.Settings.VIEW_MODE, ViewItems.ALL);
        out.registerSetting(appeng.api.config.Settings.SORT_DIRECTION, SortDir.ASCENDING);

        out.readFromNBT(is.getOrCreateTag().copy());
        return out;
    }

    @Override
    public String getEncryptionKey(ItemStack item) {
        final CompoundTag tag = item.getOrCreateTag();
        return tag.getString("encryptionKey");
    }

    @Override
    public void setEncryptionKey(ItemStack item, String encKey, String name) {
        final CompoundTag tag = item.getOrCreateTag();
        tag.putString("encryptionKey", encKey);
        tag.putString("name", name);
    }

    /**
     * get a previously stored {@link ItemStack} from a WirelessTerminal
     *
     * @param hostItem the Terminal to load from
     * @param slot     the location where the item is stored
     * @return the stored Item or {@link ItemStack}.EMPTY if it wasn't found
     */
    public ItemStack getSavedSlot(ItemStack hostItem, String slot) {
        if(!(hostItem.getItem() instanceof ItemWT) || hostItem.getTag() == null) return ItemStack.EMPTY;
        return ItemStack.fromTag(hostItem.getTag().getCompound(slot));
    }

    /**
     * store an {@link ItemStack} in a WirelessTerminal
     * this will overwrite any previously existing tags in slot
     *
     * @param hostItem  the Terminal to store in
     * @param savedItem the item to store
     * @param slot      the location where the stored item will be
     */
    public void setSavedSlot(ItemStack hostItem, ItemStack savedItem, String slot) {
        if(!(hostItem.getItem() instanceof ItemWT)) return;
        CompoundTag wctTag = hostItem.getTag();
        if(savedItem.isEmpty()) {
            if(wctTag == null) return;
            wctTag.remove(slot);
        } else {
            if(wctTag == null) wctTag = new CompoundTag();
            wctTag.put(slot, savedItem.toTag(new CompoundTag()));
        }
        hostItem.setTag(wctTag);
    }

    /**
     * get a previously stored boolean from a WirelessTerminal
     *
     * @param hostItem the Terminal to load from
     * @return the boolean or false if it wasn't found
     */
    public boolean getBoolean(ItemStack hostItem, String key) {
        if(!(hostItem.getItem() instanceof ItemWT) || hostItem.getTag() == null) return false;
        return hostItem.getTag().getBoolean(key);
    }

    /**
     * store a boolean in a WirelessTerminal
     * this will overwrite any previously existing tags in slot
     *
     * @param hostItem the Terminal to store in
     * @param b        the boolean to store
     * @param key      the location where the stored item will be
     */
    public void setBoolean(ItemStack hostItem, boolean b, String key) {
        if(!(hostItem.getItem() instanceof ItemWT)) return;
        CompoundTag wctTag = hostItem.getTag();
        if(wctTag == null) wctTag = new CompoundTag();
        wctTag.putBoolean(key, b);
        hostItem.setTag(wctTag);
    }
}