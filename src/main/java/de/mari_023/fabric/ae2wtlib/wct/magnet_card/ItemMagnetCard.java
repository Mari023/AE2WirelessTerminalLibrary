package de.mari_023.fabric.ae2wtlib.wct.magnet_card;

import de.mari_023.fabric.ae2wtlib.TextConstants;
import de.mari_023.fabric.ae2wtlib.AE2wtlib;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import java.util.List;

public class ItemMagnetCard extends Item {

    public ItemMagnetCard() {
        super(new FabricItemSettings().tab(AE2wtlib.ITEM_GROUP).stacksTo(1));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(final ItemStack stack, final Level world, final List<Component> lines, final TooltipFlag advancedTooltips) {
        super.appendHoverText(stack, world, lines, advancedTooltips);
        lines.add(TextConstants.MAGNETCARD_TOOLTIP);
    }

    public static void saveMagnetSettings(ItemStack magnetCardHolder, MagnetSettings magnetSettings) {
        ItemStack magnetCard = ItemWT.getSavedSlot(magnetCardHolder, "magnetCard");
        magnetCard.getOrCreateTag().put("magnet_settings", magnetSettings.toTag());
        ItemWT.setSavedSlot(magnetCardHolder, magnetCard, "magnetCard");
    }

    public static MagnetSettings loadMagnetSettings(ItemStack magnetCardHolder) {
        ItemStack magnetCard = ItemWT.getSavedSlot(magnetCardHolder, "magnetCard");
        if(magnetCard.isEmpty()) return new MagnetSettings();
        return new MagnetSettings((CompoundTag) magnetCard.getOrCreateTag().get("magnet_settings"));
    }

    public static boolean isActiveMagnet(ItemStack magnetCardHolder) {
        if(magnetCardHolder.isEmpty()) return false;
        MagnetSettings settings = loadMagnetSettings(magnetCardHolder);
        return settings.magnetMode == MagnetMode.PICKUP_INVENTORY || settings.magnetMode == MagnetMode.PICKUP_ME;
    }

    public static boolean isPickupME(ItemStack magnetCardHolder) {
        if(magnetCardHolder.isEmpty()) return false;
        return loadMagnetSettings(magnetCardHolder).magnetMode == MagnetMode.PICKUP_ME;
    }
}