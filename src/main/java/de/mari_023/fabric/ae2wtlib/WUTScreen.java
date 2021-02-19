package de.mari_023.fabric.ae2wtlib;

import appeng.client.gui.implementations.MEMonitorableScreen;
import appeng.container.implementations.MEPortableCellContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class WUTScreen extends MEMonitorableScreen<MEPortableCellContainer> {

    public WUTScreen(MEPortableCellContainer container, PlayerInventory playerInventory, Text title) {
        super(container, playerInventory, title);
    }
}