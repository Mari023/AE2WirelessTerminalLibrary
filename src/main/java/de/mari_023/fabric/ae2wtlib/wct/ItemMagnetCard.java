package de.mari_023.fabric.ae2wtlib.wct;

import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
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

    public static void saveMagnetSettings(ItemStack is, MagnetSettings magnetSettings) {
        is.getTag().put("magnet_settings", magnetSettings.toTag());
    }

    public static MagnetSettings loadMagnetSettings(ItemStack is) {
        if(is.getTag() == null) return new MagnetSettings(null);
        return new MagnetSettings((CompoundTag) is.getTag().get("magnet_settings"));
    }
}