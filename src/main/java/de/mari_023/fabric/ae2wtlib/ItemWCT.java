package de.mari_023.fabric.ae2wtlib;

import appeng.api.config.Actionable;
import appeng.api.config.SortDir;
import appeng.api.config.SortOrder;
import appeng.api.config.ViewItems;
import appeng.api.features.ILocatable;
import appeng.api.util.IConfigManager;
import appeng.container.ContainerLocator;
import appeng.core.AEConfig;
import appeng.core.Api;
import appeng.core.localization.GuiText;
import appeng.core.localization.PlayerMessages;
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
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;

public class ItemWCT extends ItemWT {

    public ItemWCT(Settings props) {
        super(AEConfig.instance().getWirelessTerminalBattery(), props);
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
            WCTContainer.open(player, ContainerLocator.forHand(player, hand));
        } else {
            player.sendSystemMessage(PlayerMessages.DeviceNotPowered.get(), Util.NIL_UUID);
        }
    }

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
        return is.getItem() instanceof ItemWCT;
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
}