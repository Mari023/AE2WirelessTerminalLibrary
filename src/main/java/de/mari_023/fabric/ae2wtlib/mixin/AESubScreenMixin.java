package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.client.gui.implementations.AESubScreen;
import de.mari_023.fabric.ae2wtlib.terminal.WTGuiObject;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AESubScreen.class, remap = false)
public class AESubScreenMixin {

    @Mutable
    @Shadow
    @Final
    private ScreenHandlerType<?> previousMenuType;

    @Mutable
    @Shadow
    @Final
    private ItemStack previousMenuIcon;

    @Inject(method = "<init>(Ljava/lang/Object;)V", at = @At(value = "TAIL"))
    public void serverPacketData(Object containerHost, CallbackInfo ci) {
        if(containerHost instanceof WTGuiObject) {
            previousMenuType = ((WTGuiObject) containerHost).getType();
            previousMenuIcon = ((WTGuiObject) containerHost).getIcon();
        }
    }
}
