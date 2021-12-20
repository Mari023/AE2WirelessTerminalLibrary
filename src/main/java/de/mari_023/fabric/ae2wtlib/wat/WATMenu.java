package de.mari_023.fabric.ae2wtlib.wat;

import appeng.api.config.SecurityPermissions;
import appeng.menu.implementations.InterfaceTerminalMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.RestrictedInputSlot;
import de.mari_023.fabric.ae2wtlib.AE2wtlibSlotSemantics;
import de.mari_023.fabric.ae2wtlib.TextConstants;
import de.mari_023.fabric.ae2wtlib.terminal.IWTInvHolder;
import de.mari_023.fabric.ae2wtlib.terminal.WTInventory;
import de.mari_023.fabric.ae2wtlib.wut.ItemWUT;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Function;

public class WATMenu extends InterfaceTerminalMenu implements IWTInvHolder {

    public static final MenuType<WATMenu> TYPE = MenuTypeBuilder.create(WATMenu::new, WATMenuHost.class).requirePermission(SecurityPermissions.BUILD).build("wireless_pattern_access_terminal");

    private final WATMenuHost witGUIObject;

    public WATMenu(int id, final Inventory ip, final WATMenuHost anchor) {
        super(TYPE, id, ip, anchor, true);
        witGUIObject = anchor;

        AppEngSlot infinityBoosterCardSlot = new AppEngSlot(new WTInventory(getPlayerInventory(), witGUIObject.getItemStack(), this), WTInventory.INFINITY_BOOSTER_CARD) {
            @Override
            public List<Component> getCustomTooltip(Function<ItemStack, List<Component>> getItemTooltip, ItemStack carriedItem) {
                return TextConstants.BOOSTER_SLOT;
            }
        };
        infinityBoosterCardSlot.setIcon(RestrictedInputSlot.PlacableItemType.UPGRADES.icon);
        addSlot(infinityBoosterCardSlot, AE2wtlibSlotSemantics.INFINITY_BOOSTER_CARD);
    }

    public boolean isWUT() {
        return witGUIObject.getItemStack().getItem() instanceof ItemWUT;
    }
}