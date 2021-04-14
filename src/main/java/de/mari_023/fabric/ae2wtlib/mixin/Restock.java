package de.mari_023.fabric.ae2wtlib.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class Restock {
    @Shadow
    public abstract boolean isEmpty();

    @Shadow public abstract void setCount(int count);

    @Shadow public abstract int getMaxCount();

    @Inject(method = "useOnBlock", at = @At(value = "RETURN"), require = 1, remap = false)
    public void useOnBlockRestock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if(cir.getReturnValue().isAccepted() && !isEmpty()) {
            setCount(getMaxCount());
        }
        if(context.getWorld().isClient) return;
        System.out.println();
        System.out.println("useOnBlock");
        System.out.println("===================================================================");
        System.out.println(this);
        System.out.println(context.getStack());
        System.out.println(cir.getReturnValue());
        System.out.println("===================================================================");
        System.out.println();
    }

    @Inject(method = "use", at = @At(value = "RETURN"), require = 1, remap = false)
    public void useRestock(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if(cir.getReturnValue().getResult().isAccepted() && !isEmpty()) {
            setCount(getMaxCount());
        }
        if(world.isClient) return;
        System.out.println();
        System.out.println("use");
        System.out.println("===================================================================");
        System.out.println(this);
        System.out.println(world);
        System.out.println(user);
        System.out.println(hand);
        System.out.println(cir.getReturnValue().getResult());
        System.out.println("===================================================================");
        System.out.println();
    }
}