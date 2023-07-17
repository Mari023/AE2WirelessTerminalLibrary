package de.mari_023.ae2wtlib.mixin;

import de.mari_023.ae2wtlib.AE2wtlibEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

@Mixin(ItemStack.class)
public abstract class Restock {
    @Shadow
    public abstract int getCount();

    @Inject(method = "useOn", at = @At(value = "RETURN"))
    public void useOnBlockRestock(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if ((context.getPlayer() instanceof ServerPlayer player) && cir.getReturnValue().equals(InteractionResult.CONSUME))
            AE2wtlibEvents.restock(player, (ItemStack) (Object) this, this.getCount(), ((ItemStack) (Object) this)::setCount);
    }

    @Inject(method = "use", at = @At(value = "RETURN"))
    public void useRestock(Level world, Player user, InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if ((user instanceof ServerPlayer player) && cir.getReturnValue().getResult().equals(InteractionResult.CONSUME))
            AE2wtlibEvents.restock(player, (ItemStack) (Object) this, this.getCount(), ((ItemStack) (Object) this)::setCount);
    }
}
