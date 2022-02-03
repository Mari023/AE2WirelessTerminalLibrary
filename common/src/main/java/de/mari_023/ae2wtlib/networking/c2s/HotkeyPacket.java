package de.mari_023.ae2wtlib.networking.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.TextConstants;
import de.mari_023.ae2wtlib.networking.AE2wtlibPacket;
import de.mari_023.ae2wtlib.terminal.ItemWT;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHandler;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetMode;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetSettings;
import de.mari_023.ae2wtlib.wut.WUTHandler;

import appeng.menu.locator.MenuLocator;

public class HotkeyPacket extends AE2wtlibPacket {

    public static final String NAME = "hotkey";

    public HotkeyPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    public HotkeyPacket(String type) {
        super(createBuffer());
        buf.writeUtf(type);
    }

    @Override
    public void processPacketData(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer))
            return;
        String terminalName = buf.readUtf(32767);
        if (terminalName.equalsIgnoreCase("toggleRestock")) {
            CraftingTerminalHandler craftingTerminalHandler = CraftingTerminalHandler
                    .getCraftingTerminalHandler(serverPlayer);
            ItemStack terminal = craftingTerminalHandler.getCraftingTerminal();
            if (terminal.isEmpty())
                return;
            ItemWT.setBoolean(terminal, !ItemWT.getBoolean(terminal, "restock"), "restock");
            MenuLocator locator = craftingTerminalHandler.getLocator();
            if (locator != null)
                WUTHandler.updateClientTerminal(serverPlayer, locator, terminal.getTag());

            if (ItemWT.getBoolean(terminal, "restock"))
                serverPlayer.displayClientMessage(TextConstants.RESTOCK_ON, true);
            else
                serverPlayer.displayClientMessage(TextConstants.RESTOCK_OFF, true);
        } else if (terminalName.equalsIgnoreCase("toggleMagnet")) {
            ItemStack terminal = CraftingTerminalHandler.getCraftingTerminalHandler(serverPlayer)
                    .getCraftingTerminal();
            if (terminal.isEmpty())
                return;
            MagnetSettings settings = MagnetHandler.getMagnetSettings(terminal);
            switch (settings.magnetMode) {
                case OFF -> {
                    serverPlayer.displayClientMessage(TextConstants.HOTKEY_MAGNETCARD_INVENTORY, true);
                    settings.magnetMode = MagnetMode.PICKUP_INVENTORY;
                }
                case PICKUP_INVENTORY -> {
                    serverPlayer.displayClientMessage(TextConstants.HOTKEY_MAGNETCARD_ME, true);
                    settings.magnetMode = MagnetMode.PICKUP_ME;
                }
                case PICKUP_ME -> {
                    serverPlayer.displayClientMessage(TextConstants.HOTKEY_MAGNETCARD_OFF, true);
                    settings.magnetMode = MagnetMode.OFF;
                }
            }
            MagnetHandler.saveMagnetSettings(terminal, settings);
        } else {
            MenuLocator locator = WUTHandler.findTerminal(serverPlayer, terminalName);

            if (locator == null)
                return;

            ItemStack terminal = WUTHandler.getItemStackFromLocator(serverPlayer, locator);
            WUTHandler.setCurrentTerminal(serverPlayer, locator, terminal, terminalName);
            WUTHandler.wirelessTerminals.get(terminalName).item().tryOpen(serverPlayer, locator, terminal);
        }
    }

    @Override
    public String getPacketName() {
        return NAME;
    }
}
