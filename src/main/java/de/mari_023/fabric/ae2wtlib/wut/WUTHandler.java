package de.mari_023.fabric.ae2wtlib.wut;

import appeng.menu.MenuLocator;
import de.mari_023.fabric.ae2wtlib.TextConstants;
import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import de.mari_023.fabric.ae2wtlib.ae2wtlibConfig;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.terminal.WTGuiObject;
import de.mari_023.fabric.ae2wtlib.trinket.TrinketsHelper;
import de.mari_023.fabric.ae2wtlib.wct.ItemWCT;
import de.mari_023.fabric.ae2wtlib.wit.ItemWIT;
import de.mari_023.fabric.ae2wtlib.wpt.ItemWPT;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WUTHandler {

    public static String getCurrentTerminal(ItemStack wirelessUniversalTerminal) {
        if(!(wirelessUniversalTerminal.getItem() instanceof ItemWT) || wirelessUniversalTerminal.getNbt() == null)
            return "noTerminal";
        if(!(wirelessUniversalTerminal.getItem() instanceof ItemWUT)) {
            if(wirelessUniversalTerminal.getItem() instanceof ItemWCT) return "crafting";
            else if(wirelessUniversalTerminal.getItem() instanceof ItemWPT) return "pattern";
            else if(wirelessUniversalTerminal.getItem() instanceof ItemWIT) return "interface";
            else return "noTerminal";
        }
        String currentTerminal = wirelessUniversalTerminal.getNbt().getString("currentTerminal");

        if(wirelessTerminals.containsKey(currentTerminal)) return currentTerminal;
        for(String terminal : terminalNames)
            if(wirelessUniversalTerminal.getNbt().getBoolean(terminal)) {
                currentTerminal = terminal;
                wirelessUniversalTerminal.getNbt().putString("currentTerminal", currentTerminal);
                break;
            }
        return currentTerminal;
    }

    public static void setCurrentTerminal(PlayerEntity playerEntity, int slot, ItemStack itemStack, String terminal) {
        if(!hasTerminal(itemStack, terminal)) return;
        assert itemStack.getNbt() != null;
        itemStack.getNbt().putString("currentTerminal", terminal);
        updateClientTerminal((ServerPlayerEntity) playerEntity, slot, itemStack.getNbt());
    }

    public static boolean hasTerminal(ItemStack itemStack, String terminal) {
        if(!terminalNames.contains(terminal)) return false;
        if(itemStack.getNbt() == null) return false;
        return itemStack.getNbt().getBoolean(terminal);
    }

    public static void cycle(PlayerEntity playerEntity, int slot, ItemStack itemStack) {
        if(itemStack.getNbt() == null) return;
        String nextTerminal = getCurrentTerminal(itemStack);
        do {
            int i = terminalNames.indexOf(nextTerminal) + 1;
            if(i == terminalNames.size()) i = 0;
            nextTerminal = terminalNames.get(i);
        } while(!itemStack.getNbt().getBoolean(nextTerminal));
        itemStack.getNbt().putString("currentTerminal", nextTerminal);
        updateClientTerminal((ServerPlayerEntity) playerEntity, slot, itemStack.getNbt());
    }

    public static void updateClientTerminal(ServerPlayerEntity playerEntity, int slot, NbtCompound tag) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(slot);
        buf.writeNbt(tag);
        ServerPlayNetworking.send(playerEntity, new Identifier(ae2wtlib.MOD_NAME, "update_wut"), buf);
    }

    public static void open(final PlayerEntity player, final MenuLocator locator) {
        int slot = locator.getItemIndex();
        ItemStack is;
        if(slot >= 100 && slot < 200 && ae2wtlibConfig.INSTANCE.allowTrinket())
            is = TrinketsHelper.getTrinketsInventory(player).getStackInSlot(slot - 100);
        else is = player.getInventory().getStack(slot);

        if(is.getNbt() == null) return;
        String currentTerminal = getCurrentTerminal(is);
        if(!wirelessTerminals.containsKey(currentTerminal)) {
            player.sendMessage(TextConstants.TERMINAL_EMPTY, false);
            return;
        }
        ContainerOpener terminal = wirelessTerminals.get(currentTerminal).containerOpener;
        terminal.tryOpen(player, locator, is);
    }

    public static final Map<String, WTDefinition> wirelessTerminals = new HashMap<>();
    public static final List<String> terminalNames = new ArrayList<>();

    public static void addTerminal(String name, ContainerOpener open, WTGUIObjectFactory wtguiObjectFactory) {
        if(terminalNames.contains(name)) return;
        wirelessTerminals.put(name, new WTDefinition(open, wtguiObjectFactory));
        terminalNames.add(name);
    }

    @FunctionalInterface
    public interface ContainerOpener {
        void tryOpen(PlayerEntity player, MenuLocator locator, ItemStack stack);
    }

    @FunctionalInterface
    public interface WTGUIObjectFactory {
        WTGuiObject create(final PlayerEntity ep, int inventorySlot, final ItemStack is);
    }
}