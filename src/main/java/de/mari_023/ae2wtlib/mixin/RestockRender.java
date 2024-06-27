package de.mari_023.ae2wtlib.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import appeng.util.ReadableNumberConverter;

import de.mari_023.ae2wtlib.AE2wtlibComponents;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;

@Mixin(value = GuiGraphics.class, remap = false)
public abstract class RestockRender {
    @Shadow
    public abstract void renderItemDecorations(Font pFont, ItemStack pStack, int pX, int pY, @Nullable String pText);

    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At(value = "HEAD"), cancellable = true)
    public void renderGuiItemOverlay(Font font, ItemStack stack, int x, int y, CallbackInfo ci) {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().player.isCreative())
            return;
        CraftingTerminalHandler handler = CraftingTerminalHandler
                .getCraftingTerminalHandler(Minecraft.getInstance().player);
        ItemStack hostItem = handler.getCraftingTerminal();
        if (stack.getCount() == 1 || !handler.isRestockAble(stack)
                || !hostItem.getOrDefault(AE2wtlibComponents.RESTOCK, false))
            return;
        renderItemDecorations(font, stack, x, y, ReadableNumberConverter.format(handler.getAccessibleAmount(stack), 3));
        ci.cancel();
    }
}
