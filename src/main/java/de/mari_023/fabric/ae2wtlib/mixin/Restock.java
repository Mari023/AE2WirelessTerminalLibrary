package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEItemKey;
import appeng.me.helpers.PlayerSource;
import de.mari_023.fabric.ae2wtlib.AE2wtlib;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.wct.CraftingTerminalHandler;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
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
    public abstract int getMaxStackSize();

    @Shadow
    public abstract int getCount();

    @Shadow
    public abstract ItemStack copy();

    @Inject(method = "useOn", at = @At(value = "RETURN"))
    public void useOnBlockRestock(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if(!context.getLevel().isClientSide() && cir.getReturnValue().equals(InteractionResult.CONSUME))
            restock(context.getPlayer());
    }

    @Inject(method = "use", at = @At(value = "RETURN"))
    public void useRestock(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if(!world.isClientSide() && cir.getReturnValue().getResult().equals(InteractionResult.CONSUME)) restock(user);
    }

    private void restock(Player playerEntity) {
        if(isEmpty() && !playerEntity.isCreative()) return;
        CraftingTerminalHandler CTHandler = CraftingTerminalHandler.getCraftingTerminalHandler(playerEntity);
        if(!CTHandler.inRange() || !ItemWT.getBoolean(CTHandler.getCraftingTerminal(), "restock") || CTHandler.getItemStorageChannel() == null)
            return;
        int toAdd = getMaxStackSize() - getCount();
        if(toAdd == 0) return;
        ItemStack request = copy();
        long extractedItems = CTHandler.getItemStorageChannel().extract(AEItemKey.of(request), toAdd, Actionable.MODULATE, new PlayerSource(playerEntity, CTHandler.getSecurityStation()));
        if(extractedItems > Integer.MAX_VALUE)
            throw new IllegalStateException("Extracted amount cannot be larger than requested amount");
        setCount(getCount() + (int) extractedItems);
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(playerEntity.getInventory().findSlotMatchingUnusedItem((ItemStack) (Object) this));
        buf.writeInt(getCount());
        ServerPlayNetworking.send((ServerPlayer) playerEntity, new ResourceLocation(AE2wtlib.MOD_NAME, "update_restock"), buf);
    }
}