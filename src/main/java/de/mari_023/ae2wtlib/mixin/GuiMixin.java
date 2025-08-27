package de.mari_023.ae2wtlib.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import appeng.util.ReadableNumberConverter;

import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;

@Mixin(Gui.class)
public class GuiMixin {
    @Final
    @Shadow
    private Minecraft minecraft;

    @Inject(method = "renderSlot(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/client/DeltaTracker;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V"), cancellable = true)
    public void restockOverlay(GuiGraphics guiGraphics, int x, int y, DeltaTracker deltaTracker, Player player,
            ItemStack stack, int seed, CallbackInfo ci) {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().player.isCreative())
            return;
        CraftingTerminalHandler handler = CraftingTerminalHandler
                .getCraftingTerminalHandler(Minecraft.getInstance().player);
        if (!handler.isRestockEnabled() || stack.getCount() == 1 || !handler.isRestockAble(stack))
            return;
        String number = ReadableNumberConverter.format(handler.getAccessibleAmount(stack), 3);
        if (number.startsWith(","))
            number = 0 + number;
        guiGraphics.renderItemDecorations(minecraft.font, stack, x, y, number);
        ci.cancel();
    }
}
