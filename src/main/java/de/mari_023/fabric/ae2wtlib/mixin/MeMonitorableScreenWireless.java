package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.client.gui.implementations.MEMonitorableScreen;
import appeng.container.implementations.WirelessCraftingStatusContainer;
import de.mari_023.fabric.ae2wtlib.wct.WCTScreen;
import de.mari_023.fabric.ae2wtlib.wpt.WPTScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MEMonitorableScreen.class)
public class MeMonitorableScreenWireless {

    @Inject(method = "showCraftingStatus", at = @At(value = "INVOKE"), cancellable = true, remap = false)
    private void showWirelessCraftingStatus(CallbackInfo ci) {
        if(!((Object) this instanceof WCTScreen) && !((Object) this instanceof WPTScreen)) return;
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIdentifier(Registry.SCREEN_HANDLER.getId(WirelessCraftingStatusContainer.TYPE));
        ClientPlayNetworking.send(new Identifier("ae2wtlib", "switch_gui"), buf);
        try {
            ci.cancel();
        } catch(Exception ignored) {}
    }
}