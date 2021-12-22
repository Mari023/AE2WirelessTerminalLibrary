package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.menu.implementations.MenuTypeBuilder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MenuTypeBuilder.class, remap = false)
public class MenuTypeBuilderMixin<I> {

    /*@Shadow
    @Final
    private Class<I> hostInterface;

    @Inject(method = "getHostFromPlayerInventory", at = @At(value = "HEAD"), cancellable = true)
    public void serverPacketData(Player player, MenuLocator locator, CallbackInfoReturnable<I> cir) {
        int slot = locator.getItemIndex();
        ItemStack it;

        if(slot >= 100 && slot < 200 && AE2wtlibConfig.INSTANCE.allowTrinket())
            it = TrinketsHelper.getTrinketsInventory(player).getStackInSlot(slot - 100);
        else it = player.getInventory().getItem(slot);

        if(it.isEmpty()) return;

        String currentTerminal = WUTHandler.getCurrentTerminal(it);
        if(WUTHandler.terminalNames.contains(currentTerminal))
            cir.setReturnValue(hostInterface.cast(WUTHandler.wirelessTerminals.get(currentTerminal).wTMenuHostFactory().create(player, locator.getItemIndex(), it, (p, subMenu) -> ((WirelessTerminalItem) it.getItem()).openFromInventory(p, locator.getItemIndex()))));
    }*/
}
