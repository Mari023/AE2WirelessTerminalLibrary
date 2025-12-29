package de.mari_023.ae2wtlib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import de.mari_023.ae2wtlib.AE2wtlibEvents;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    public ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Inject(method = "drop(Z)V", at = @At(value = "TAIL"))
    public void restockDrop(boolean all, CallbackInfo ci, @Local(name = "selected") ItemStack item) {
        if (item.isEmpty())
            return;

        AE2wtlibEvents.restock((ServerPlayer) (Object) this, item, item.getCount(),
                (itemStack) -> getInventory().setItem(getInventory().getSelectedSlot(), itemStack));
    }
}
