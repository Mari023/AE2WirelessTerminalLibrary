package de.mari_023.ae2wtlib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.AE2wtlibEvents;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {
    @Inject(method = "loadProjectile(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;ZZ)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;split(I)Lnet/minecraft/world/item/ItemStack;", shift = At.Shift.AFTER))
    private static void restockArrows(LivingEntity livingEntity, ItemStack stack, ItemStack projectile, boolean hasAmmo,
            boolean isCreative, CallbackInfoReturnable<Boolean> cir) {
        if (!(livingEntity instanceof ServerPlayer player))
            return;
        AE2wtlibEvents.restock(player, projectile, projectile.getCount(), (itemStack) -> {
        });
    }
}
