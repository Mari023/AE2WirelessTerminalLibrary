package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.container.AEBaseContainer;
import appeng.container.ContainerLocator;
import appeng.core.sync.network.INetworkInfo;
import appeng.core.sync.packets.InventoryActionPacket;
import de.mari_023.fabric.ae2wtlib.util.WirelessCraftAmountContainer;
import de.mari_023.fabric.ae2wtlib.wct.WCTContainer;
import de.mari_023.fabric.ae2wtlib.wpt.WPTContainer;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InventoryActionPacket.class, remap = false)
public class InvActionPacket {

    @Inject(method = "serverPacketData", at = @At(value = "INVOKE", target = "Lappeng/container/ContainerOpener;openContainer(Lnet/minecraft/screen/ScreenHandlerType;Lnet/minecraft/entity/player/PlayerEntity;Lappeng/container/ContainerLocator;)Z"))
    public void serverPacketData(INetworkInfo manager, PlayerEntity player, CallbackInfo ci) {
        if(player.currentScreenHandler instanceof WCTContainer || player.currentScreenHandler instanceof WPTContainer) {
            final AEBaseContainer baseContainer = (AEBaseContainer) player.currentScreenHandler;
            final ContainerLocator locator = baseContainer.getLocator();
            WirelessCraftAmountContainer.open(player, locator);

            if(player.currentScreenHandler instanceof WirelessCraftAmountContainer) {
                final WirelessCraftAmountContainer cca = (WirelessCraftAmountContainer) player.currentScreenHandler;

                if(baseContainer.getTargetStack() != null) {
                    cca.getCraftingItem().setStack(baseContainer.getTargetStack().asItemStackRepresentation());
                    cca.setItemToCraft(baseContainer.getTargetStack());
                }
                cca.sendContentUpdates();
            }
        }
    }
}