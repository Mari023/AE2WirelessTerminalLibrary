package de.mari_023.ae2wtlib.wct.magnet_card.config;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import de.mari_023.ae2wtlib.TextConstants;

import appeng.api.config.IncludeExclude;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.implementations.AESubScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.IconButton;

public class MagnetScreen extends AEBaseScreen<MagnetMenu> {
    public MagnetScreen(MagnetMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        AESubScreen.addBackButton(menu, "back", widgets);

        widgets.add("pickup_mode", new IconButton(button -> menu.togglePickupMode()) {
            @Override
            protected Icon getIcon() {
                return icon(menu.getMagnetHost().getPickupMode());
            }

            @Override
            public Component getMessage() {
                return TextConstants.getPickupMode(menu.getMagnetHost().getPickupMode());
            }
        });

        widgets.add("insert_mode", new IconButton(button -> menu.toggleInsertMode()) {
            @Override
            protected Icon getIcon() {
                return icon(menu.getMagnetHost().getInsertMode());
            }

            @Override
            public Component getMessage() {
                return TextConstants.getInsertMode(menu.getMagnetHost().getInsertMode());
            }
        });

        widgets.add("copy_up", new IconButton(button -> menu.copyUp()) {
            @Override
            protected Icon getIcon() {
                return Icon.ARROW_UP;
            }

            @Override
            public Component getMessage() {
                return TextConstants.COPY_PICKUP;
            }
        });

        widgets.add("copy_down", new IconButton(button -> menu.copyDown()) {
            @Override
            protected Icon getIcon() {
                return Icon.ARROW_DOWN;
            }

            @Override
            public Component getMessage() {
                return TextConstants.COPY_INSERT;
            }
        });

        widgets.add("switch", new IconButton(button -> menu.switchInsertPickup()) {
            @Override
            protected Icon getIcon() {
                return Icon.SCHEDULING_ROUND_ROBIN;
            }

            @Override
            public Component getMessage() {
                return TextConstants.SWITCH;
            }
        });
    }

    private Icon icon(IncludeExclude includeExclude) {
        return switch (includeExclude) {
            case WHITELIST -> Icon.WHITELIST;
            case BLACKLIST -> Icon.BLACKLIST;
        };
    }
}
