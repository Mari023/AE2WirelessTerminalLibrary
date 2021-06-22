package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionHost;
import appeng.api.storage.data.IAEItemStack;
import appeng.me.helpers.PlayerSource;
import appeng.util.item.AEItemStack;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.wct.CraftingTerminalHandler;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
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

    @Shadow
    public abstract ItemStack copy();

    @Inject(method = "useOnBlock", at = @At(value = "RETURN"))
    public void useOnBlockRestock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if(!context.getWorld().isClient()) restock(context.getPlayer(), cir.getReturnValue());
    }

    @Inject(method = "use", at = @At(value = "RETURN"))
    public void useRestock(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if(!world.isClient()) restock(user, cir.getReturnValue().getResult());
    }

    private void restock(PlayerEntity playerEntity, ActionResult result) {
        if(result.equals(ActionResult.CONSUME) && !isEmpty() && !playerEntity.isCreative()) {
            CraftingTerminalHandler CTHandler = CraftingTerminalHandler.getCraftingTerminalHandler(playerEntity);
            if(CTHandler.inRange() && ItemWT.getBoolean(CTHandler.getCraftingTerminal(), "restock") && CTHandler.getItemStorageChannel() != null) {
                try {
                    int toAdd = getMaxCount() - getCount();
                    if(toAdd == 0) return;
                    ItemStack request = copy();
                    request.setCount(toAdd);
                    IAEItemStack stack = CTHandler.getItemStorageChannel().extractItems(AEItemStack.fromItemStack(request), Actionable.MODULATE, new PlayerSource(playerEntity, (IActionHost) CTHandler.getSecurityStation()));
                    if(stack == null) return;
                    ItemStack extraction = stack.createItemStack();
                    int extractedItems = 0;
                    if(extraction != null && !extraction.isEmpty()) extractedItems = extraction.getCount();
                    setCount(getCount() + extractedItems);
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeInt(playerEntity.inventory.getSlotWithStack((ItemStack) (Object) this));
                    buf.writeInt(getCount());
                    ServerPlayNetworking.send((ServerPlayerEntity) playerEntity, new Identifier("ae2wtlib", "update_restock"), buf);
                } catch(NullPointerException ignored) {}
            }
        }
    }
}