package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.core.Api;
import appeng.me.helpers.PlayerSource;
import appeng.util.item.AEItemStack;
import de.mari_023.fabric.ae2wtlib.wct.CraftingTerminalHandler;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.ItemMagnetCard;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public class PlayerInventoryInsertStack {

    @Shadow
    @Final
    public PlayerEntity player;

    @Inject(method = "insertStack(Lnet/minecraft/item/ItemStack;)Z", at = @At(value = "INVOKE"), require = 1, allow = 1, cancellable = true)
    public void insertStackInME(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if(stack.isEmpty()) return;
        CraftingTerminalHandler CTHandler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
        ItemStack terminal = CTHandler.getCraftingTerminal();
        if(ItemMagnetCard.isPickupME(terminal) && CTHandler.inRange()) {
            IAEItemStack leftover = ((IStorageGrid) CTHandler.getTargetGrid().getCache(IStorageGrid.class)).getInventory(Api.instance().storage().getStorageChannel(IItemStorageChannel.class)).injectItems(AEItemStack.fromItemStack(stack), Actionable.MODULATE, new PlayerSource(player, (IActionHost) CTHandler.getSecurityStation()));
            if(leftover == null || leftover.createItemStack().isEmpty()) {
                stack.setCount(0);
                cir.setReturnValue(true);
            } else stack.setCount(leftover.createItemStack().getCount());
        }
    }
}