package de.mari_023.fabric.ae2wtlib;

import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;

public class AE2wtlibSlotSemantics {

    private AE2wtlibSlotSemantics() {}

    public static final SlotSemantic HELMET = add("HELMET");
    public static final SlotSemantic CHESTPLATE = add("CHESTPLATE");
    public static final SlotSemantic LEGGINGS = add("LEGGINGS");
    public static final SlotSemantic BOOTS = add("BOOTS");

    public static final SlotSemantic OFFHAND = add("OFFHAND");

    public static final SlotSemantic TRASH = add("TRASH");
    public static final SlotSemantic MAGNET_CARD = add("MAGNET_CARD");
    public static final SlotSemantic INFINITY_BOOSTER_CARD = add("INFINITY_BOOSTER_CARD");


    private static SlotSemantic add(String id) {
        return SlotSemantics.add("AE2WTLIB_"+id, false);
    }
}
