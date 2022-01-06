package de.mari_023.fabric.ae2wtlib.wut;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.menu.ISubMenu;
import appeng.menu.locator.MenuLocator;
import appeng.menu.locator.MenuLocators;
import de.mari_023.fabric.ae2wtlib.AE2wtlib;
import de.mari_023.fabric.ae2wtlib.AE2wtlibConfig;
import de.mari_023.fabric.ae2wtlib.TextConstants;
import de.mari_023.fabric.ae2wtlib.terminal.IUniversalWirelessTerminalItem;
import de.mari_023.fabric.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.fabric.ae2wtlib.trinket.TrinketLocator;
import de.mari_023.fabric.ae2wtlib.trinket.TrinketsHelper;
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
        if(terminal.getItem() instanceof ItemWUT) {
            String currentTerminal = terminal.getOrCreateTag().getString("currentTerminal");

            if(wirelessTerminals.containsKey(currentTerminal)) return currentTerminal;
            for(String term : terminalNames)
                if(terminal.getOrCreateTag().getBoolean(term)) {
                    currentTerminal = term;
                    terminal.getOrCreateTag().putString("currentTerminal", currentTerminal);
                    break;
                }
            return currentTerminal;
        }
        for(Map.Entry<String, WTDefinition> entry : wirelessTerminals.entrySet()) {
            if(terminal.getItem().equals(entry.getValue().item())) return entry.getKey();
        }
        return "";
    }

    public static void setCurrentTerminal(Player playerEntity, MenuLocator locator, ItemStack itemStack, String terminal) {
        if(!(itemStack.getItem() instanceof ItemWUT)) return;
        if(!hasTerminal(itemStack, terminal)) return;
        assert itemStack.getTag() != null;
        itemStack.getTag().putString("currentTerminal", terminal);
        updateClientTerminal((ServerPlayer) playerEntity, locator, itemStack.getTag());
    }

    public static boolean hasTerminal(ItemStack terminal, String terminalName) {
        if(terminal.getItem() instanceof ItemWUT) {
            if(!terminalNames.contains(terminalName)) return false;
            if(terminal.getTag() == null) return false;
            return terminal.getTag().getBoolean(terminalName);
        }
        return terminal.getItem().equals(wirelessTerminals.get(terminalName).item());
    }

    public static void cycle(Player playerEntity, MenuLocator locator, ItemStack itemStack, boolean isHandlingRightClick) {
        if(itemStack.getTag() == null) return;
        String nextTerminal = getCurrentTerminal(itemStack);
        do {
            int i;
            if(isHandlingRightClick) {
                i = terminalNames.indexOf(nextTerminal) - 1;
                if(i == -1) i = terminalNames.size() - 1;
            } else {
                i = terminalNames.indexOf(nextTerminal) + 1;
                if(i == terminalNames.size()) i = 0;
            }
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

    public static MenuLocator findTerminal(Player player, String terminalName) {
        if(AE2wtlibConfig.INSTANCE.allowTrinket()) {
            TrinketLocator trinketLocator = TrinketsHelper.findTerminal(player, terminalName);
            if(trinketLocator != null) return trinketLocator;
        }

        for(int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if(hasTerminal(player.getInventory().getItem(i), terminalName)) return MenuLocators.forInventorySlot(i);
        }
        return null;
    }

    public static ItemStack getItemStackFromLocator(Player player, @Nullable MenuLocator locator) {
        if(locator == null) return ItemStack.EMPTY;
        if(locator instanceof TrinketLocator trinketLocator) return trinketLocator.locateItem(player);
        ItemMenuHost host = locator.locate(player, WTMenuHost.class);
        if(host == null) return ItemStack.EMPTY;
        return host.getItemStack();
    }

    public static final Map<String, WTDefinition> wirelessTerminals = new HashMap<>();
    public static final List<String> terminalNames = new ArrayList<>();

    public static void addTerminal(String name, ContainerOpener open, WTMenuHostFactory WTMenuHostFactory, MenuType<?> menuType, IUniversalWirelessTerminalItem item) {
        if(terminalNames.contains(name)) return;
        wirelessTerminals.put(name, new WTDefinition(open, WTMenuHostFactory, menuType, item));
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