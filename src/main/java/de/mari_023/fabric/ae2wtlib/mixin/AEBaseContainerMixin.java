package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.api.networking.security.IActionHost;
import appeng.container.AEBaseContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = AEBaseContainer.class, remap = false)
public interface AEBaseContainerMixin {

    @Invoker("getActionHost")
    IActionHost invokeGetActionHost();
}