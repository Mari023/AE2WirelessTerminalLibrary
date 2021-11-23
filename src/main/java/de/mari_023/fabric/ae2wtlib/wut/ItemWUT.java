package de.mari_023.fabric.ae2wtlib.wut;

import appeng.core.AEConfig;
import appeng.menu.MenuLocator;
import de.mari_023.fabric.ae2wtlib.TextConstants;
import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import de.mari_023.fabric.ae2wtlib.ae2wtlibConfig;
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
        super(() -> AEConfig.instance().getWirelessTerminalBattery().getAsDouble() * ae2wtlibConfig.INSTANCE.WUTBatterySizeMultiplier(), new FabricItemSettings().group(ae2wtlib.ITEM_GROUP).maxCount(1));
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
        return super.getChargeRate() * ae2wtlibConfig.INSTANCE.WUTChargeRateMultiplier();
    }

    @Override
    public void open(final PlayerEntity player, final MenuLocator locator) {
        WUTHandler.open(player, locator);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(final ItemStack stack, final World world, final List<Text> lines, final TooltipContext advancedTooltips) {
        lines.add(TextConstants.UNIVERSAL);
        if(WUTHandler.hasTerminal(stack, "crafting"))
            lines.add(TextConstants.CRAFTING);
        if(WUTHandler.hasTerminal(stack, "interface"))
            lines.add(TextConstants.INTERFACE);
        if(WUTHandler.hasTerminal(stack, "pattern"))
            lines.add(TextConstants.PATTERN);
        super.appendTooltip(stack, world, lines, advancedTooltips);
    }
}