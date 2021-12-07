package de.mari_023.fabric.ae2wtlib.wut;

import appeng.core.AEConfig;
import appeng.menu.MenuLocator;
import de.mari_023.fabric.ae2wtlib.TextConstants;
import de.mari_023.fabric.ae2wtlib.AE2wtlib;
import de.mari_023.fabric.ae2wtlib.AE2wtlibConfig;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class ItemWUT extends ItemWT {

    public ItemWUT() {
        super(() -> AEConfig.instance().getWirelessTerminalBattery().getAsDouble() * AE2wtlibConfig.INSTANCE.WUTBatterySizeMultiplier(), new FabricItemSettings().group(AE2wtlib.ITEM_GROUP).maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(final World w, final PlayerEntity player, final Hand hand) {
        /*if(player.isSneaking()) {
            //TODO open menu to select terminal
        return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
        } else*/
        return super.use(w, player, hand);
    }

    @Override
    public double getChargeRate() {
        return super.getChargeRate() * AE2wtlibConfig.INSTANCE.WUTChargeRateMultiplier();
    }

    @Override
    public boolean open(final PlayerEntity player, final MenuLocator locator) {
        return WUTHandler.open(player, locator);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(final ItemStack stack, final World world, final List<Text> lines, final TooltipContext advancedTooltips) {
        lines.add(TextConstants.UNIVERSAL);
        if(WUTHandler.hasTerminal(stack, "crafting"))
            lines.add(TextConstants.CRAFTING);
        if(WUTHandler.hasTerminal(stack, "pattern_access"))
            lines.add(TextConstants.PATTERN_ACCESS);
        if(WUTHandler.hasTerminal(stack, "pattern_encoding"))
            lines.add(TextConstants.PATTERN_ENCODING);
        super.appendTooltip(stack, world, lines, advancedTooltips);
    }
}