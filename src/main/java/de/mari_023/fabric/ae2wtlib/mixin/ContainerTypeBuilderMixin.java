package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.container.ContainerLocator;
import appeng.container.implementations.ContainerTypeBuilder;
import de.mari_023.fabric.ae2wtlib.Config;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.wct.WCTGuiObject;
import de.mari_023.fabric.ae2wtlib.wit.WITGuiObject;
import de.mari_023.fabric.ae2wtlib.wpt.WPTGuiObject;
import de.mari_023.fabric.ae2wtlib.wut.WUTHandler;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ContainerTypeBuilder.class, remap = false)
public class ContainerTypeBuilderMixin<I> {

    @Shadow
    @Final
    private Class<I> hostInterface;

    @Inject(method = "getHostFromPlayerInventory", at = @At(value = "HEAD"), cancellable = true)
    public void serverPacketData(PlayerEntity player, ContainerLocator locator, CallbackInfoReturnable<I> cir) {
        int slot = locator.getItemIndex();
        ItemStack it;

        if(slot >= 100 && slot < 200 && Config.allowTrinket())
            it = TrinketsApi.getTrinketsInventory(player).getStack(slot - 100);
        else it = player.inventory.getStack(slot);

        if(it.isEmpty()) return;

        String currentTerminal = WUTHandler.getCurrentTerminal(it);//get the current Terminal, we need to differentiate to return a different WxTgUIObject
        //TODO do something generic, I don't want to hardcode everything
        if(hostInterface.isAssignableFrom(WCTGuiObject.class) && currentTerminal.equals("crafting"))
            cir.setReturnValue(hostInterface.cast(new WCTGuiObject((ItemWT) it.getItem(), it, player, locator.getItemIndex())));

        if(hostInterface.isAssignableFrom(WPTGuiObject.class) && currentTerminal.equals("pattern"))
            cir.setReturnValue(hostInterface.cast(new WPTGuiObject((ItemWT) it.getItem(), it, player, locator.getItemIndex())));

        if(hostInterface.isAssignableFrom(WITGuiObject.class) && currentTerminal.equals("interface"))
            cir.setReturnValue(hostInterface.cast(new WITGuiObject((ItemWT) it.getItem(), it, player, locator.getItemIndex())));
    }
}
