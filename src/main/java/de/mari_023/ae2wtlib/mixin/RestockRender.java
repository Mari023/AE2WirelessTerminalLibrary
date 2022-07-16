package de.mari_023.ae2wtlib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import de.mari_023.ae2wtlib.terminal.ItemWT;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;

import appeng.util.ReadableNumberConverter;

@OnlyIn(Dist.CLIENT)
@Mixin(ItemRenderer.class)
public class RestockRender {
    /** TODO maybe hook into {@link Gui#renderSlot(int, int, float, Player, ItemStack, int)} instead **/
    @Inject(method = "renderGuiItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At(value = "INVOKE"), cancellable = true)
    public void renderGuiItemOverlay(Font renderer, ItemStack stack, int x, int y, CallbackInfo ci) {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().player.isCreative())
            return;
        CraftingTerminalHandler handler = CraftingTerminalHandler
                .getCraftingTerminalHandler(Minecraft.getInstance().player);
        if (!handler.isRestockable(stack)
                || !ItemWT.getBoolean(handler.getCraftingTerminal(), "restock") || !handler.inRange())
            return;
        var amount = handler.getAccessibleAmount(stack);
        if (amount == 1)
            return;
        ((ItemRenderer) (Object) this).renderGuiItemDecorations(renderer, stack, x, y,
                ReadableNumberConverter.format(amount, 3));
        ci.cancel();
    }
}
