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

    private void restock(Player player) {
        if (isEmpty() && !player.isCreative())
            return;
        CraftingTerminalHandler cTHandler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
        if (!cTHandler.inRange() || !ItemWT.getBoolean(cTHandler.getCraftingTerminal(), "restock")
                || cTHandler.getTargetGrid() == null || cTHandler.getTargetGrid().getStorageService() == null)
            return;
        int toAdd = getMaxStackSize() / 2 - getCount();
        if (toAdd == 0)
            return;

        long changed;
        if (toAdd > 0)
            changed = cTHandler.getTargetGrid().getStorageService().getInventory().extract(
                    AEItemKey.of((ItemStack) (Object) this), toAdd, Actionable.MODULATE,
                    new PlayerSource(player, cTHandler.getSecurityStation()));
        else
            changed = - cTHandler.getTargetGrid().getStorageService().getInventory().insert(
                    AEItemKey.of((ItemStack) (Object) this), - toAdd, Actionable.MODULATE,
                    new PlayerSource(player, cTHandler.getSecurityStation()));

        setCount(getCount() + (int) changed);
        ServerNetworkManager.sendToClient((ServerPlayer) player, new UpdateRestockPacket(
                player.getInventory().findSlotMatchingUnusedItem((ItemStack) (Object) this), getCount()));
    }
}
