package de.mari_023.fabric.ae2wtlib.wut;

import appeng.items.tools.powered.WirelessCraftingTerminalItem;
import appeng.menu.ISubMenu;
import appeng.menu.locator.MenuLocator;
import appeng.menu.locator.MenuLocators;
import de.mari_023.fabric.ae2wtlib.AE2wtlib;
import de.mari_023.fabric.ae2wtlib.TextConstants;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.fabric.ae2wtlib.wat.ItemWAT;
import de.mari_023.fabric.ae2wtlib.wet.ItemWET;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class WUTHandler {

    public static String getCurrentTerminal(ItemStack terminal) {
        if(terminal.getItem() instanceof WirelessCraftingTerminalItem) return "crafting";
        if(!(terminal.getItem() instanceof ItemWT) || terminal.getTag() == null)
            return "";
        if(!(terminal.getItem() instanceof ItemWUT)) {
            if(terminal.getItem() instanceof ItemWET) return "pattern_encoding";
            else if(terminal.getItem() instanceof ItemWAT) return "pattern_access";
            else return "";
        }
        String currentTerminal = terminal.getTag().getString("currentTerminal");

        if(wirelessTerminals.containsKey(currentTerminal)) return currentTerminal;
        for(String term : terminalNames)
            if(terminal.getTag().getBoolean(term)) {
                currentTerminal = term;
                terminal.getTag().putString("currentTerminal", currentTerminal);
                break;
            }
        return currentTerminal;
    }

    public static void setCurrentTerminal(Player playerEntity, MenuLocator locator, ItemStack itemStack, String terminal) {
        if(!hasTerminal(itemStack, terminal)) return;
        assert itemStack.getTag() != null;
        itemStack.getTag().putString("currentTerminal", terminal);
        updateClientTerminal((ServerPlayer) playerEntity, locator, itemStack.getTag());
    }

    public static boolean hasTerminal(ItemStack itemStack, String terminal) {
        if(!terminalNames.contains(terminal)) return false;
        if(itemStack.getTag() == null) return false;
        return itemStack.getTag().getBoolean(terminal);
    }

    public static void cycle(Player playerEntity, MenuLocator locator, ItemStack itemStack) {
        if(itemStack.getTag() == null) return;
        String nextTerminal = getCurrentTerminal(itemStack);
        do {
            int i = terminalNames.indexOf(nextTerminal) + 1;
            if(i == terminalNames.size()) i = 0;
            nextTerminal = terminalNames.get(i);
        } while(!itemStack.getTag().getBoolean(nextTerminal));
        itemStack.getTag().putString("currentTerminal", nextTerminal);
        updateClientTerminal((ServerPlayer) playerEntity, locator, itemStack.getTag());
    }

    public static void updateClientTerminal(ServerPlayer playerEntity, MenuLocator locator, CompoundTag tag) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        MenuLocators.writeToPacket(buf, locator);
        buf.writeNbt(tag);
        ServerPlayNetworking.send(playerEntity, new ResourceLocation(AE2wtlib.MOD_NAME, "update_wut"), buf);
    }

    public static boolean open(final Player player, final MenuLocator locator) {
        WTMenuHost host = locator.locate(player, WTMenuHost.class);
        if(host == null) return false;
        ItemStack is = host.getItemStack();

        if(is.getTag() == null) return false;
        String currentTerminal = getCurrentTerminal(is);
        if(!wirelessTerminals.containsKey(currentTerminal)) {
            player.displayClientMessage(TextConstants.TERMINAL_EMPTY, false);
            return false;
        }
        return wirelessTerminals.get(currentTerminal).containerOpener().tryOpen(player, locator, is);
    }

    public static final Map<String, WTDefinition> wirelessTerminals = new HashMap<>();
    public static final List<String> terminalNames = new ArrayList<>();

    public static void addTerminal(String name, ContainerOpener open, WTMenuHostFactory WTMenuHostFactory, MenuType<?> menuType) {
        if(terminalNames.contains(name)) return;
        wirelessTerminals.put(name, new WTDefinition(open, WTMenuHostFactory, menuType));
        terminalNames.add(name);
    }

    @FunctionalInterface
    public interface ContainerOpener {
        boolean tryOpen(Player player, MenuLocator locator, ItemStack stack);
    }

    @FunctionalInterface
    public interface WTMenuHostFactory {
        WTMenuHost create(final Player ep, @Nullable Integer inventorySlot, final ItemStack is, BiConsumer<Player, ISubMenu> returnToMainMenu);
    }
}