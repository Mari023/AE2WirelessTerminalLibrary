package de.mari_023.fabric.ae2wtlib.mixin;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Screen.class)
public interface ScreenMixin {
    @Accessor("drawables")
    List<Drawable> getDrawables();
}
