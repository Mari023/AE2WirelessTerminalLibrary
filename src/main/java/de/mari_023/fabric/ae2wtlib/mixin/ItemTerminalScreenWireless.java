package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.client.gui.me.items.ItemTerminalScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;

@Environment(EnvType.CLIENT)
@Mixin(value = ItemTerminalScreen.class, remap = false)
public class ItemTerminalScreenWireless {

    /*@Inject(method = "showCraftingStatus()V", at = @At(value = "INVOKE"), cancellable = true)//FIXME
    private void showWirelessCraftingStatus(CallbackInfo ci) {
        if(!((Object) this instanceof WCTScreen) && !((Object) this instanceof WPTScreen)) return;
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIdentifier(Registry.SCREEN_HANDLER.getId(WirelessCraftingStatusContainer.TYPE));
        ClientPlayNetworking.send(new Identifier("ae2wtlib", "switch_gui"), buf);
        ci.cancel();
    }*/
}