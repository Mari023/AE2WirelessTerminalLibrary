package de.mari_023.ae2wtlib.mixin;

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

import de.mari_023.ae2wtlib.networking.ServerNetworkManager;
import de.mari_023.ae2wtlib.networking.s2c.UpdateRestockPacket;
import de.mari_023.ae2wtlib.terminal.ItemWT;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEItemKey;
import appeng.me.helpers.PlayerSource;

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
        if (!context.getLevel().isClientSide() && cir.getReturnValue().equals(InteractionResult.CONSUME)
                && context.getPlayer() != null)
            restock(context.getPlayer());
    }

    @Inject(method = "use", at = @At(value = "RETURN"))
    public void useRestock(Level world, Player user, InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (!world.isClientSide() && cir.getReturnValue().getResult().equals(InteractionResult.CONSUME))
            restock(user);
    }

    private void restock(Player playerEntity) {
        if (isEmpty() && !playerEntity.isCreative())
            return;
        CraftingTerminalHandler cTHandler = CraftingTerminalHandler.getCraftingTerminalHandler(playerEntity);
        if (!cTHandler.inRange() || !ItemWT.getBoolean(cTHandler.getCraftingTerminal(), "restock")
                || cTHandler.getTargetGrid() == null || cTHandler.getTargetGrid().getStorageService() == null)
            return;
        int toAdd = getMaxStackSize() - getCount();
        if (toAdd == 0)
            return;
        ItemStack request = copy();
        long extractedItems = cTHandler.getTargetGrid().getStorageService().getInventory().extract(
                AEItemKey.of(request), toAdd, Actionable.MODULATE,
                new PlayerSource(playerEntity, cTHandler.getSecurityStation()));
        if (extractedItems > Integer.MAX_VALUE)
            throw new IllegalStateException("Extracted amount cannot be larger than requested amount");
        setCount(getCount() + (int) extractedItems);
        ServerNetworkManager.sendToClient((ServerPlayer) playerEntity, new UpdateRestockPacket(
                playerEntity.getInventory().findSlotMatchingUnusedItem((ItemStack) (Object) this), getCount()));
    }
}
