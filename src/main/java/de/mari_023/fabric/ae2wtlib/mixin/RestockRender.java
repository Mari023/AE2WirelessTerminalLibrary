package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.util.ReadableNumberConverter;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.wct.CraftingTerminalHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public class RestockRender {

    @Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V", at = @At(value = "INVOKE"), cancellable = true)
    public void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, CallbackInfo ci) {
        if(MinecraftClient.getInstance().player == null) return;
        CraftingTerminalHandler handler = CraftingTerminalHandler.getCraftingTerminalHandler(MinecraftClient.getInstance().player);
        if(MinecraftClient.getInstance().player.isCreative() || !ItemWT.getBoolean(handler.getCraftingTerminal(), "restock") || !handler.inRange() || stack.getCount() == 1)
            return;
        ((ItemRenderer) (Object) this).renderGuiItemOverlay(renderer, stack, x, y, ReadableNumberConverter.INSTANCE.toSlimReadableForm(handler.getAccessibleAmount(stack)));
        ci.cancel();
    }
}