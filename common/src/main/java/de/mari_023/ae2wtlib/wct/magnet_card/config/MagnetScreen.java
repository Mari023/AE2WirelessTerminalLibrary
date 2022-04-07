package de.mari_023.ae2wtlib.wct.magnet_card.config;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

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
        });

        widgets.add("insert_mode", new IconButton(button -> menu.toggleInsertMode()) {
            @Override
            protected Icon getIcon() {
                return icon(menu.getMagnetHost().getInsertMode());
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
