package de.mari_023.fabric.ae2wtlib.mixin;

import me.ultrablacklinux.minemenufabric.client.screen.MineMenuSelectScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(value = MineMenuSelectScreen.class, remap = false)
public interface MineMenuMixin {
    @Accessor("circleEntries")
    void setCircleEntries(int circleEntries);
}