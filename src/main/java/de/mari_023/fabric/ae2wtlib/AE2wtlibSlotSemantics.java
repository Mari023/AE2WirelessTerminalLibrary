package de.mari_023.fabric.ae2wtlib;

import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;

public class AE2wtlibSlotSemantics {

    private AE2wtlibSlotSemantics() {}

    public static final SlotSemantic HELMET = register("HELMET");
    public static final SlotSemantic CHESTPLATE = register("CHESTPLATE");
    public static final SlotSemantic LEGGINGS = register("LEGGINGS");
    public static final SlotSemantic BOOTS = register("BOOTS");

    public static final SlotSemantic OFFHAND = register("OFFHAND");

    public static final SlotSemantic TRASH = register("TRASH");
    public static final SlotSemantic MAGNET_CARD = register("MAGNET_CARD");
    public static final SlotSemantic INFINITY_BOOSTER_CARD = register("INFINITY_BOOSTER_CARD");


    private static SlotSemantic register(String id) {
        return SlotSemantics.register("AE2WTLIB_"+id, false);
    }
}
