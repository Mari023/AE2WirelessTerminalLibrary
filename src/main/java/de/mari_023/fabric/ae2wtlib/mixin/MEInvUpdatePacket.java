package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.core.sync.packets.MEInventoryUpdatePacket;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MEInventoryUpdatePacket.class, remap = false)
public class MEInvUpdatePacket {

    /*@Shadow
    @Final
    private List<IAEItemStack> list;
    @Shadow
    @Final
    private byte ref;

    @Environment(EnvType.CLIENT)
    @Inject(method = "clientPacketData", at = @At(value = "TAIL"))
    public void clientPacketData(INetworkInfo manager, PlayerEntity player, CallbackInfo ci) {
        if(MinecraftClient.getInstance().currentScreen instanceof WirelessCraftConfirmScreen)
            ((WirelessCraftConfirmScreen) MinecraftClient.getInstance().currentScreen).postUpdate(list, ref);
    }*/
}