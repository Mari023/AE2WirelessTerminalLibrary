package de.mari_023.ae2wtlib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;

import de.mari_023.ae2wtlib.AE2wtlibEvents;

@Mixin(ProjectileWeaponItem.class)
public class ProjectileWeaponItemMixin {
    @Inject(method = "useAmmo(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;Z)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"))
    private static void useAmmo(ItemStack p_331207_, ItemStack stack, LivingEntity livingEntity, boolean p_330934_,
            CallbackInfoReturnable<ItemStack> cir) {
        if (!(livingEntity instanceof ServerPlayer player))
            return;
        ItemStack projectile = player.getProjectile(stack);
        AE2wtlibEvents.restock(player, projectile, projectile.getCount(), (itemStack) -> {
        });
    }
}
