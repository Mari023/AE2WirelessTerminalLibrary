package de.mari_023.fabric.ae2wtlib.wut;

import appeng.core.AEConfig;
import appeng.core.definitions.AEItems;
import appeng.menu.locator.MenuLocator;
import de.mari_023.fabric.ae2wtlib.AE2wtlib;
import de.mari_023.fabric.ae2wtlib.AE2wtlibConfig;
import de.mari_023.fabric.ae2wtlib.TextConstants;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ItemWUT extends ItemWT {

    public ItemWUT() {
        super(() -> AEConfig.instance().getWirelessTerminalBattery().getAsDouble() * AE2wtlibConfig.INSTANCE.WUTBatterySizeMultiplier(), new FabricItemSettings().tab(AE2wtlib.ITEM_GROUP).stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level w, final Player player, final InteractionHand hand) {
        if(WUTHandler.getCurrentTerminal(player.getItemInHand(hand)).equals("noTerminal")) {
            player.sendMessage(TextConstants.TERMINAL_EMPTY, Util.NIL_UUID);
            return new InteractionResultHolder<>(InteractionResult.FAIL, player.getItemInHand(hand));
        }
        /*if(player.isShiftKeyDown()) {
            if(w.isClientSide()) Minecraft.getInstance().setScreen(new WUTSelectScreen(player.getItemInHand(hand)));
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
        } else*/ return super.use(w, player, hand);
    }

    @Override
    public double getChargeRate(ItemStack stack) {
        return 800d * (AE2wtlibConfig.INSTANCE.WUTChargeRateMultiplier() + 1 + getUpgrades(stack).getInstalledUpgrades(AEItems.ENERGY_CARD));
    }

    @Override
    public boolean open(final Player player, ItemStack stack, final MenuLocator locator) {
        return WUTHandler.open(player, locator);
    }

    @Override
    public MenuType<?> getMenuType(ItemStack stack) {
        return WUTHandler.wirelessTerminals.get(WUTHandler.getCurrentTerminal(stack)).menuType();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(final ItemStack stack, final Level world, final List<Component> lines, final TooltipFlag advancedTooltips) {
        lines.add(TextConstants.UNIVERSAL);
        if(WUTHandler.hasTerminal(stack, "crafting"))
            lines.add(TextConstants.CRAFTING);
        if(WUTHandler.hasTerminal(stack, "pattern_access"))
            lines.add(TextConstants.PATTERN_ACCESS);
        if(WUTHandler.hasTerminal(stack, "pattern_encoding"))
            lines.add(TextConstants.PATTERN_ENCODING);
        super.appendHoverText(stack, world, lines, advancedTooltips);
    }
}