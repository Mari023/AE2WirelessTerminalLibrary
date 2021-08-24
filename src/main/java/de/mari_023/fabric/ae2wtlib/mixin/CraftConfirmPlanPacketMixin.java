package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.container.me.crafting.CraftingPlanSummary;
import appeng.container.me.crafting.WirelessCraftConfirmContainer;
import appeng.core.sync.network.INetworkInfo;
import appeng.core.sync.packets.CraftConfirmPlanPacket;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CraftConfirmPlanPacket.class, remap = false)
public class CraftConfirmPlanPacketMixin {

    @Shadow
    @Final
    private CraftingPlanSummary plan;

    @Inject(method = "clientPacketData", at = @At(value = "TAIL"))
    public void serverPacketData(INetworkInfo manager, PlayerEntity player, CallbackInfo ci) {
        if(player.currentScreenHandler instanceof WirelessCraftConfirmContainer)
            ((WirelessCraftConfirmContainer) player.currentScreenHandler).setPlan(plan);
    }
}
