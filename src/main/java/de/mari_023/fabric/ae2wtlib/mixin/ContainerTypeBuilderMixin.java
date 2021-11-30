package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.menu.MenuLocator;
import appeng.menu.implementations.MenuTypeBuilder;
import de.mari_023.fabric.ae2wtlib.AE2wtlibConfig;
import de.mari_023.fabric.ae2wtlib.trinket.TrinketsHelper;
import de.mari_023.fabric.ae2wtlib.wut.WUTHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MenuTypeBuilder.class, remap = false)
public class ContainerTypeBuilderMixin<I> {

    @Shadow
    @Final
    private Class<I> hostInterface;

    @Inject(method = "getHostFromPlayerInventory", at = @At(value = "HEAD"), cancellable = true)
    public void serverPacketData(PlayerEntity player, MenuLocator locator, CallbackInfoReturnable<I> cir) {
        int slot = locator.getItemIndex();
        ItemStack it;

        if(slot >= 100 && slot < 200 && AE2wtlibConfig.INSTANCE.allowTrinket())
            it = TrinketsHelper.getTrinketsInventory(player).getStackInSlot(slot - 100);
        else it = player.getInventory().getStack(slot);

        if(it.isEmpty()) return;

        String currentTerminal = WUTHandler.getCurrentTerminal(it);
        if(WUTHandler.terminalNames.contains(currentTerminal))
            cir.setReturnValue(hostInterface.cast(WUTHandler.wirelessTerminals.get(currentTerminal).wTMenuHostFactory().create(player, locator.getItemIndex(), it, (p, subMenu) -> ((WirelessTerminalItem) it.getItem()).openFromInventory(p, locator.getItemIndex()))));
    }
}
