package de.mari_023.fabric.ae2wtlib.mixin;

import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.wct.CraftingTerminalHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public class RestockRender {
    /**
     * @author Mari_023
     */
    @Overwrite
    public void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y) {
        if(MinecraftClient.getInstance().player == null) return;
        CraftingTerminalHandler handler = CraftingTerminalHandler.getCraftingTerminalHandler(MinecraftClient.getInstance().player);
        if(MinecraftClient.getInstance().player.isCreative() || !ItemWT.getBoolean(handler.getCraftingTerminal(), "restock") || !handler.inRange()) {
            ((ItemRenderer) (Object) this).renderGuiItemOverlay(renderer, stack, x, y, null);
            return;
        }
        ((ItemRenderer) (Object) this).renderGuiItemOverlay(renderer, stack, x, y, "Restock");
    }
}