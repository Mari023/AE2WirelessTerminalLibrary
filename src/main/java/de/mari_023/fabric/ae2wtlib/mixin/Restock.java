package de.mari_023.fabric.ae2wtlib.mixin;

import de.mari_023.fabric.ae2wtlib.wct.CraftingTerminalHandler;
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

    @Shadow
    public abstract void setCount(int count);

    @Shadow
    public abstract int getMaxCount();

    @Shadow
    public abstract int getCount();

    @Inject(method = "useOnBlock", at = @At(value = "RETURN"), require = 1, remap = false)
    public void useOnBlockRestock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        restock(context.getPlayer(), cir.getReturnValue());
    }

    @Inject(method = "use", at = @At(value = "RETURN"), require = 1, remap = false)
    public void useRestock(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        restock(user, cir.getReturnValue().getResult());
    }

    private void restock(PlayerEntity playerEntity, ActionResult result) {
        if(result.isAccepted() && !isEmpty()) {
            CraftingTerminalHandler cthandler = CraftingTerminalHandler.getCraftingTerminalHandler(playerEntity);
            if(cthandler.inRange()) {

                int toAdd = getMaxCount() - getCount();
                setCount(getCount() + toAdd);
            }
        }
    }
}