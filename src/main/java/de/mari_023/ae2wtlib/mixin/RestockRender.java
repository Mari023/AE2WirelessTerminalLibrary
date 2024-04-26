package de.mari_023.ae2wtlib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import appeng.util.ReadableNumberConverter;

import de.mari_023.ae2wtlib.AE2WTLibComponents;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;

@OnlyIn(Dist.CLIENT)
@Mixin(value = GuiGraphics.class, remap = false)
public class RestockRender {
    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At(value = "HEAD"), cancellable = true)
    public void renderGuiItemOverlay(Font font, ItemStack stack, int x, int y, CallbackInfo ci) {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().player.isCreative())
            return;
        CraftingTerminalHandler handler = CraftingTerminalHandler
                .getCraftingTerminalHandler(Minecraft.getInstance().player);
        ItemStack hostItem = handler.getCraftingTerminal();
        if (stack.getCount() == 1 || !handler.isRestockAble(stack)
                || !hostItem.getOrDefault(AE2WTLibComponents.RESTOCK, false))
            return;
        ((GuiGraphics) (Object) this).renderItemDecorations(font, stack, x, y,
                ReadableNumberConverter.format(handler.getAccessibleAmount(stack), 3));
        ci.cancel();
    }
}
