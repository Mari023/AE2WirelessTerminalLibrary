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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.InvocationTargetException;

@Mixin(value = ContainerTypeBuilder.class, remap = false)
public class ContainerTypeBuilderMixin<I> {

    @Shadow
    @Final
    private Class<I> hostInterface;

    @Inject(method = "getHostFromPlayerInventory", at = @At(value = "HEAD"), cancellable = true)
    public void serverPacketData(PlayerEntity player, ContainerLocator locator, CallbackInfoReturnable<I> cir) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        int slot = locator.getItemIndex();
        ItemStack it;

        if(slot >= 100 && slot < 200 && Config.allowTrinket())
            it = TrinketsApi.getTrinketsInventory(player).getStack(slot - 100);
        else it = player.inventory.getStack(slot);

        if(it.isEmpty()) return;

        String currentTerminal = WUTHandler.getCurrentTerminal(it);//get the current Terminal, we need to differentiate to return a different WxTgUIObject
        I result = terminalCheck(WCTGuiObject.class, currentTerminal, "crafting", it, player, locator);
        if (result == null)
            result = terminalCheck(WPTGuiObject.class, currentTerminal, "pattern", it, player, locator);
        if (result == null)
            result = terminalCheck(WITGuiObject.class, currentTerminal, "interface", it, player, locator);
        cir.setReturnValue(result);
    }

    @Unique
    public I terminalCheck(Class<?> clazz, String ct, String nt, ItemStack stack, PlayerEntity player, ContainerLocator locator) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (hostInterface.isAssignableFrom(clazz) && ct.equals(nt))
            return (hostInterface.cast(clazz.getConstructor().newInstance(stack.getItem(), stack, player, locator.getItemIndex())));
        return null;
    }
}
