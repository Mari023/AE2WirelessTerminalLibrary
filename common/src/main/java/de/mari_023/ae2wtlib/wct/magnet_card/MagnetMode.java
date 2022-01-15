package de.mari_023.ae2wtlib.wct.magnet_card;

public enum MagnetMode {
    INVALID((byte) -2), NO_CARD((byte) -1), OFF((byte) 0), PICKUP_INVENTORY((byte) 1), PICKUP_ME((byte) 2);

    public static final MagnetMode DEFAULT = OFF;

    private final byte id;

    MagnetMode(byte b) {
        id = b;
    }

    public byte getId() {
        return id;
    }

    public static MagnetMode fromByte(byte b) {
        return switch (b) {
            case -1 -> NO_CARD;
            case 0 -> OFF;
            case 1 -> PICKUP_INVENTORY;
            case 2 -> PICKUP_ME;
            default -> INVALID;
        };
    }
}
