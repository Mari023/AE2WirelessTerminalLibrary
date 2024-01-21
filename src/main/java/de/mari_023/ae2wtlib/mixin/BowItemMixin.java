package de.mari_023.ae2wtlib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import de.mari_023.ae2wtlib.AE2wtlibEvents;

@Mixin(BowItem.class)
public class BowItemMixin {
    @Inject(method = "releaseUsing(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V", shift = At.Shift.AFTER))
    public void restockArrows(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft, CallbackInfo ci) {
        if (!(livingEntity instanceof ServerPlayer player))
            return;
        ItemStack projectile = player.getProjectile(stack);
        AE2wtlibEvents.restock(player, projectile, projectile.getCount(), (itemStack) -> {
        });
    }
}
