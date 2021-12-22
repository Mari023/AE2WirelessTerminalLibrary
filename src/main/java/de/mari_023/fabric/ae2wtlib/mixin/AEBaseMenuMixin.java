package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.menu.AEBaseMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Deprecated
@Mixin(value = AEBaseMenu.class, remap = false)
public class AEBaseMenuMixin {
    @Inject(method = "lockPlayerInventorySlot", at = @At(value = "HEAD"), cancellable = true)
    public void lockPlayerInventorySlot(int invSlot, CallbackInfo ci) {
        if(invSlot >= 100) ci.cancel();
    }
}
