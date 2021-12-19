package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.util.ReadableNumberConverter;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.wct.CraftingTerminalHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public class RestockRender {

    @Inject(method = "renderGuiItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At(value = "INVOKE"), cancellable = true)
    public void renderGuiItemOverlay(Font renderer, ItemStack stack, int x, int y, CallbackInfo ci) {
        if(Minecraft.getInstance().player == null) return;
        CraftingTerminalHandler handler = CraftingTerminalHandler.getCraftingTerminalHandler(Minecraft.getInstance().player);
        if(Minecraft.getInstance().player.isCreative() || !ItemWT.getBoolean(handler.getCraftingTerminal(), "restock") || !handler.inRange() || stack.getCount() == 1)
            return;
        ((ItemRenderer) (Object) this).renderGuiItemDecorations(renderer, stack, x, y, ReadableNumberConverter.format(handler.getAccessibleAmount(stack), 3));
        ci.cancel();
    }
}