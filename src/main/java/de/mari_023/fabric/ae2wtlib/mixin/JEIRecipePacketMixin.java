package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.core.sync.packets.JEIRecipePacket;
import de.mari_023.fabric.ae2wtlib.wpt.WPTContainer;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JEIRecipePacket.class, remap = false)
public class JEIRecipePacketMixin {

    @Inject(method = "handleProcessing", at = @At(value = "HEAD"))
    public void handleProcessing(ScreenHandler con, Recipe<?> recipe, CallbackInfo ci) {
        if (con instanceof WPTContainer wptContainer) {
            if (!wptContainer.isCraftingMode()) {
                wptContainer.setProcessingResult(recipe.getOutput());
            }
        }
    }
}
