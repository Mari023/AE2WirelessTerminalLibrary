package de.mari_023.fabric.ae2wtlib.terminal;

import de.mari_023.fabric.ae2wtlib.TextConstants;
import de.mari_023.fabric.ae2wtlib.AE2wtlib;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import java.util.List;

public class ItemInfinityBooster extends Item {
    public ItemInfinityBooster() {
        super(new FabricItemSettings().tab(AE2wtlib.ITEM_GROUP).stacksTo(1));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(final ItemStack stack, final Level world, final List<Component> lines, final TooltipFlag advancedTooltips) {
        super.appendHoverText(stack, world, lines, advancedTooltips);
        lines.add(TextConstants.BOOSTER);
    }
}