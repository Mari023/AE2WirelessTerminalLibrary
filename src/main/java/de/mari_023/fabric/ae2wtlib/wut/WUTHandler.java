package de.mari_023.fabric.ae2wtlib.wut;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WUTHandler {
    public static void cycle(ItemStack itemStack) {
        if(itemStack.getTag() == null) return;
        String nextTerminal = itemStack.getTag().getString("currentTerminal");
        do {
            int i = terminalNames.indexOf(nextTerminal) + 1;
            if(i == terminalNames.size()) i = 0;
            nextTerminal = terminalNames.get(i);
        } while(!itemStack.getTag().getBoolean(nextTerminal));
        itemStack.getTag().putString("currentTerminal", nextTerminal);
    }

    public static void open(final PlayerEntity player, final Hand hand) {
        ItemStack is;
        switch(hand) {
            case MAIN_HAND:
                is = player.inventory.getMainHandStack();
                break;
            case OFF_HAND:
                is = player.inventory.offHand.get(0);
                break;
            default:
                throw new IllegalStateException("There is no such hand: " + hand);
        }
        if(is.getTag() == null) return;
        String currentTerminal = is.getTag().getString("currentTerminal");

        if(!wirelessTerminals.containsKey(currentTerminal)) {
            for(String terminal : terminalNames) {
                if(is.getTag().getBoolean(terminal)) {
                    currentTerminal = terminal;
                    is.getTag().putString("currentTerminal", currentTerminal);
                    break;
                }
            }
            if(!wirelessTerminals.containsKey(currentTerminal)) {
                player.sendMessage(new LiteralText("This terminal does not contain any other Terminals"), false);
                return;
            }
        }

        wirelessTerminals.get(currentTerminal).open(player, hand);
    }


    private static final HashMap<String, containerOpener> wirelessTerminals = new HashMap<>();
    private static final List<String> terminalNames = new ArrayList<>();

    public static void addTerminal(String Name, containerOpener open) {
        if(terminalNames.contains(Name)) return;
        wirelessTerminals.put(Name, open);
        terminalNames.add(Name);
    }

    @FunctionalInterface
    public interface containerOpener {
        void open(final PlayerEntity player, final Hand hand);
    }
}