package de.mari_023.fabric.ae2wtlib.wut;

import appeng.container.ContainerLocator;
import appeng.core.AEConfig;
import de.mari_023.fabric.ae2wtlib.Config;
import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import de.mari_023.fabric.ae2wtlib.client.MineMenuIntegration;
import de.mari_023.fabric.ae2wtlib.terminal.IInfinityBoosterCardHolder;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class ItemWUT extends ItemWT implements IInfinityBoosterCardHolder {

    public ItemWUT() {
        super(AEConfig.instance().getWirelessTerminalBattery(), new FabricItemSettings().group(ae2wtlib.ITEM_GROUP).maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(final World w, final PlayerEntity player, final Hand hand) {
        if(player.isSneaking()) {
            if(w.isClient() && Config.allowMineMenu()) MineMenuIntegration.openMineMenu(player.getStackInHand(hand));
            else;//This is here to trick java into not loading MineMenuIntegration when it can't
        } else openWirelessTerminalGui(player.getStackInHand(hand), player, hand);
        return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
    }

    @Override
    public void open(final PlayerEntity player, final ContainerLocator locator) {
        WUTHandler.open(player, locator);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(final ItemStack stack, final World world, final List<Text> lines, final TooltipContext advancedTooltips) {
        lines.add(new TranslatableText("item.ae2wtlib.wireless_universal_terminal.desc").fillStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        if(WUTHandler.hasTerminal(stack, "crafting"))
            lines.add(new TranslatableText("item.ae2wtlib.wireless_crafting_terminal").fillStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        if(WUTHandler.hasTerminal(stack, "interface"))
            lines.add(new TranslatableText("item.ae2wtlib.wireless_interface_terminal").fillStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        if(WUTHandler.hasTerminal(stack, "pattern"))
            lines.add(new TranslatableText("item.ae2wtlib.wireless_pattern_terminal").fillStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        super.appendTooltip(stack, world, lines, advancedTooltips);
    }
}