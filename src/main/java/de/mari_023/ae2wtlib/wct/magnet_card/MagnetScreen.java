package de.mari_023.ae2wtlib.wct.magnet_card;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import appeng.api.config.IncludeExclude;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.implementations.AESubScreen;
import appeng.client.gui.style.ScreenStyle;

import de.mari_023.ae2wtlib.TextConstants;
import de.mari_023.ae2wtlib.terminal.Icon;
import de.mari_023.ae2wtlib.terminal.IconButton;

public class MagnetScreen extends AEBaseScreen<MagnetMenu> {
    public MagnetScreen(MagnetMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        AESubScreen.addBackButton(menu, "back", widgets);
        if (menu.getMagnetHost() == null)
            return;

        widgets.add("pickup_mode", new IconButton(button -> menu.togglePickupMode(), Icon.YES) {
            @Override
            protected Icon getIcon() {
                return icon(menu.getMagnetHost().getPickupMode());
            }

            @Override
            public Component getMessage() {
                return TextConstants.getPickupMode(menu.getMagnetHost().getPickupMode());
            }
        });

        widgets.add("insert_mode", new IconButton(button -> menu.toggleInsertMode(), Icon.YES) {
            @Override
            protected Icon getIcon() {
                return icon(menu.getMagnetHost().getInsertMode());
            }

            @Override
            public Component getMessage() {
                return TextConstants.getInsertMode(menu.getMagnetHost().getInsertMode());
            }
        });

        widgets.add("copy_up", new IconButton(button -> menu.copyUp(), Icon.UP).withTooltip(TextConstants.COPY_PICKUP));

        widgets.add("copy_down",
                new IconButton(button -> menu.copyDown(), Icon.DOWN).withTooltip(TextConstants.COPY_INSERT));

        widgets.add("switch",
                new IconButton(button -> menu.switchInsertPickup(), Icon.SWITCH).withTooltip(TextConstants.SWITCH));
    }

    private Icon icon(IncludeExclude includeExclude) {
        return switch (includeExclude) {
            case WHITELIST -> Icon.YES;
            case BLACKLIST -> Icon.NO;
        };
    }

    // Added to remove the VerticalButtonBar for this Screen - Rid
    @Override
    protected boolean shouldAddToolbar() {
        return false;
    }
}
