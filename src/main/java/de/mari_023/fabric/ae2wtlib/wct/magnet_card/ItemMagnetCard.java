package de.mari_023.fabric.ae2wtlib.wct.magnet_card;

import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;

import java.util.List;

public class ItemMagnetCard extends Item {

    public ItemMagnetCard() {
        super(new FabricItemSettings().group(ae2wtlib.ITEM_GROUP).maxCount(1));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(final ItemStack stack, final World world, final List<Text> lines, final TooltipContext advancedTooltips) {
        super.appendTooltip(stack, world, lines, advancedTooltips);
        lines.add(new TranslatableText("item.ae2wtlib.magnet_card.desc"));
    }

    public static void saveMagnetSettings(ItemStack magnetCardHolder, MagnetSettings magnetSettings) {
        ItemStack magnetCard = ItemWT.getSavedSlot(magnetCardHolder, "magnetCard");
        magnetCard.getOrCreateNbt().put("magnet_settings", magnetSettings.toTag());
        ItemWT.setSavedSlot(magnetCardHolder, magnetCard, "magnetCard");
    }

    public static MagnetSettings loadMagnetSettings(ItemStack magnetCardHolder) {
        ItemStack magnetCard = ItemWT.getSavedSlot(magnetCardHolder, "magnetCard");
        if(magnetCard.isEmpty()) return new MagnetSettings();
        return new MagnetSettings((NbtCompound) magnetCard.getOrCreateNbt().get("magnet_settings"));
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