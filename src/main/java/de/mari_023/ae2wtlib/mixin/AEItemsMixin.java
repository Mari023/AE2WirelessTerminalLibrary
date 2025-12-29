package de.mari_023.ae2wtlib.mixin;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import appeng.api.ids.AECreativeTabIds;
import appeng.api.ids.AEItemIds;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.ItemDefinition;

import de.mari_023.ae2wtlib.wct.ItemWCT;

@Mixin(AEItems.class)
public class AEItemsMixin {
    @Shadow
    static <T extends Item> ItemDefinition<T> item(String name, Identifier id,
            Function<Item.Properties, T> factory, ResourceKey<CreativeModeTab> main) {
        return new ItemDefinition<>("", null);
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "item(Ljava/lang/String;Lnet/minecraft/resources/Identifier;Ljava/util/function/Function;)Lappeng/core/definitions/ItemDefinition;", at = @At(value = "HEAD"), cancellable = true)
    private static <T extends Item> void replaceWirelessCraftingTerminal(String name, Identifier id,
            Function<Item.Properties, T> factory, CallbackInfoReturnable<ItemDefinition<T>> cir) {
        if (id.equals(AEItemIds.WIRELESS_CRAFTING_TERMINAL))
            cir.setReturnValue(item(name, id, p -> (T) new ItemWCT(p), AECreativeTabIds.MAIN));
    }
}
