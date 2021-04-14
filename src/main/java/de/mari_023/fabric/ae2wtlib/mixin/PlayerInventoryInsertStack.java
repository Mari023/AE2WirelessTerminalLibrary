package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.api.config.Actionable;
import appeng.api.features.ILocatable;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.core.Api;
import appeng.core.localization.PlayerMessages;
import appeng.me.helpers.PlayerSource;
import appeng.util.item.AEItemStack;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.ItemMagnetCard;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.MagnetHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryInsertStack {

    @Shadow
    @Final
    public
    PlayerEntity player;

    @Shadow
    public abstract boolean insertStack(int slot, ItemStack stack);

    /**
     * @author Mari_023
     */
    @Overwrite
    public boolean insertStack(ItemStack stack) {
        ItemStack terminal = MagnetHandler.getCraftingTerminal(player);
        if(ItemMagnetCard.isPickupME(terminal)) {
            final String unparsedKey = ((ItemWT) terminal.getItem()).getEncryptionKey(terminal);
            if(unparsedKey.isEmpty()) {
                player.sendSystemMessage(PlayerMessages.DeviceNotLinked.get(), Util.NIL_UUID);
            }
            final long parsedKey = Long.parseLong(unparsedKey);
            final ILocatable securityStation = Api.instance().registries().locatable().getLocatableBy(parsedKey);
            final IGridNode n = ((IActionHost) securityStation).getActionableNode();
            IGrid targetGrid = n.getGrid();
            IStorageGrid sg = targetGrid.getCache(IStorageGrid.class);
            IMEMonitor<IAEItemStack> itemStorage = sg.getInventory(Api.instance().storage().getStorageChannel(IItemStorageChannel.class));
            IAEItemStack leftover = itemStorage.injectItems(AEItemStack.fromItemStack(stack), Actionable.MODULATE, new PlayerSource(player, (IActionHost) securityStation));

            if(leftover == null || leftover.createItemStack().isEmpty()) {
                stack.setCount(0);
                return true;
            }
            stack.setCount(leftover.createItemStack().getCount());
        }
        return insertStack(-1, stack);
    }
}