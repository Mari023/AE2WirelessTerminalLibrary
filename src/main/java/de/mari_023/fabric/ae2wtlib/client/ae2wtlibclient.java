package de.mari_023.fabric.ae2wtlib.client;

import appeng.container.implementations.WirelessCraftConfirmContainer;
import appeng.container.implementations.WirelessCraftingStatusContainer;
import de.mari_023.fabric.ae2wtlib.util.WirelessCraftAmountContainer;
import de.mari_023.fabric.ae2wtlib.util.WirelessCraftAmountScreen;
import de.mari_023.fabric.ae2wtlib.util.WirelessCraftConfirmScreen;
import de.mari_023.fabric.ae2wtlib.util.WirelessCraftingStatusScreen;
import de.mari_023.fabric.ae2wtlib.wct.WCTContainer;
import de.mari_023.fabric.ae2wtlib.wct.WCTScreen;
import de.mari_023.fabric.ae2wtlib.wit.WITContainer;
import de.mari_023.fabric.ae2wtlib.wit.WITScreen;
import de.mari_023.fabric.ae2wtlib.wpt.WPTContainer;
import de.mari_023.fabric.ae2wtlib.wpt.WPTScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ae2wtlibclient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //WCTContainer.TYPE = ScreenHandlerRegistry.registerExtended(new Identifier("ae2wtlib", "wireless_crafting_terminal"), WCTContainer::fromNetwork);
        //WPTContainer.TYPE = ScreenHandlerRegistry.registerExtended(new Identifier("ae2wtlib", "wireless_pattern_terminal"), WPTContainer::fromNetwork);
        //WITContainer.TYPE = ScreenHandlerRegistry.registerExtended(new Identifier("ae2wtlib", "wireless_interface_terminal"), WITContainer::fromNetwork);
        //WirelessCraftingStatusContainer.TYPE = ScreenHandlerRegistry.registerExtended(new Identifier("ae2wtlib", "wireless_crafting_status"), WirelessCraftingStatusContainer::fromNetwork);
        //WirelessCraftAmountContainer.TYPE = ScreenHandlerRegistry.registerExtended(new Identifier("ae2wtlib", "wireless_craft_amount"), WirelessCraftAmountContainer::fromNetwork);
        //WirelessCraftConfirmContainer.TYPE = ScreenHandlerRegistry.registerExtended(new Identifier("ae2wtlib", "wireless_craft_confirm"), WirelessCraftConfirmContainer::fromNetwork);

        ScreenRegistry.register(WCTContainer.TYPE, WCTScreen::new);
        ScreenRegistry.register(WPTContainer.TYPE, WPTScreen::new);
        ScreenRegistry.register(WITContainer.TYPE, WITScreen::new);
        ScreenRegistry.register(WirelessCraftingStatusContainer.TYPE, WirelessCraftingStatusScreen::new);
        ScreenRegistry.register(WirelessCraftAmountContainer.TYPE, WirelessCraftAmountScreen::new);
        ScreenRegistry.register(WirelessCraftConfirmContainer.TYPE, WirelessCraftConfirmScreen::new);

        ClientPlayNetworking.registerGlobalReceiver(new Identifier("ae2wtlib", "interface_terminal"), (client, handler, buf, responseSender) -> {
            buf.retain();
            client.execute(() -> {
                if(client.player == null) return;

                final Screen screen = MinecraftClient.getInstance().currentScreen;
                if(screen instanceof WITScreen) {
                    WITScreen s = (WITScreen) screen;
                    CompoundTag tag = buf.readCompoundTag();
                    if(tag != null) s.postUpdate(tag);
                }
                buf.release();
            });
        });
    }
}