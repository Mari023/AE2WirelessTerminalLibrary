package de.mari_023.fabric.ae2wtlib.trinket;

import appeng.container.slot.AppEngSlot;

public class AppEngTrinketSlot extends AppEngSlot {

    public boolean keepVisible = false;
    public String group, slot;

    public AppEngTrinketSlot(FixedTrinketInv inv, int invSlot, int x, int y, String group, String slot) {
        super(inv, invSlot, x, y);
        this.group = group;
        this.slot = slot;
    }
}