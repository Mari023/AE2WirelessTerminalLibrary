package de.mari_023.fabric.ae2wtlib;

import appeng.items.tools.powered.powersink.AEBasePoweredItem;

import java.util.function.DoubleSupplier;

public abstract class ItemWT extends AEBasePoweredItem implements ICustomWirelessTerminalItem {

    public ItemWT(DoubleSupplier powerCapacity, Settings props) {
        super(powerCapacity, props);
    }
}