package de.mari_023.ae2wtlib.api.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import appeng.client.gui.ICompositeWidget;
import appeng.client.gui.WidgetContainer;

@Mixin(WidgetContainer.class)
public interface WidgetContainerAccessor {
    @Accessor
    Map<String, ICompositeWidget> getCompositeWidgets();
}
