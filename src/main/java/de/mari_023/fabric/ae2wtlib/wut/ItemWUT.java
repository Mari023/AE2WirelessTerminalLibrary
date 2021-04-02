package de.mari_023.fabric.ae2wtlib.wut;

import appeng.container.ContainerLocator;
import appeng.core.AEConfig;
import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import de.mari_023.fabric.ae2wtlib.terminal.IInfinityBoosterCardHolder;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.wct.WCTContainer;
import de.mari_023.fabric.ae2wtlib.wit.WITContainer;
import de.mari_023.fabric.ae2wtlib.wpt.WPTContainer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;

public class ItemWUT extends ItemWT implements IInfinityBoosterCardHolder {

    public ItemWUT() {
        super(AEConfig.instance().getWirelessTerminalBattery(), new FabricItemSettings().group(ae2wtlib.ITEM_GROUP).maxCount(1));
    }

    @Override
    public void open(final PlayerEntity player, final Hand hand) {
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
        String lastTerminal = is.getTag().getString("lastTerminal");

        while(true) {
            switch(lastTerminal) {
                case "crafting":
                    WCTContainer.open(player, ContainerLocator.forHand(player, hand));
                    return;
                case "pattern":
                    WPTContainer.open(player, ContainerLocator.forHand(player, hand));
                    return;
                case "interface":
                    WITContainer.open(player, ContainerLocator.forHand(player, hand));
                    return;
                default:
                    if(is.getTag().getBoolean("crafting")) {
                        lastTerminal = "crafting";
                    } else if(is.getTag().getBoolean("pattern")) {
                        lastTerminal = "pattern";
                    } else if(is.getTag().getBoolean("interface")) {
                        lastTerminal = "interface";
                    } else {
                        player.sendMessage(new LiteralText("This terminal does not contain any other Terminals"), false);
                        return;
                    }
            }
        }
    }
}