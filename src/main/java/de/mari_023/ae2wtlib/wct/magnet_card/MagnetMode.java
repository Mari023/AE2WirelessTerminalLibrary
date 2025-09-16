package de.mari_023.ae2wtlib.wct.magnet_card;

public enum MagnetMode {
    INVALID((byte) -2), NO_CARD((byte) -1), OFF((byte) 0), PICKUP_INVENTORY((byte) 0b01), PICKUP_ME((byte) 0b11),
    PICKUP_ME_NO_MAGNET((byte) 0b10);

    public static final MagnetMode DEFAULT = OFF;

    private final byte id;

    MagnetMode(byte b) {
        id = b;
    }

    public byte getId() {
        return id;
    }

    public boolean magnet() {
        return id > 0 && (id & 0b01) != 0;
    }

    public boolean pickupToME() {
        return id > 0 && (id & 0b10) != 0;
    }

    public MagnetMode set(boolean magnet, boolean pickupToME) {
        if (id < 0)
            return this;
        return fromByte((byte) ((magnet ? 0b01 : 0) + (pickupToME ? 0b10 : 0)));
    }

    public static MagnetMode fromByte(byte b) {
        return switch (b) {
            case 1 -> PICKUP_INVENTORY;
            case 2 -> PICKUP_ME_NO_MAGNET;
            case 3 -> PICKUP_ME;
            default -> OFF;
        };
    }
}
