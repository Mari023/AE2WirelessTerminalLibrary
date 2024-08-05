package de.mari_023.ae2wtlib.wct.magnet_card;

public enum MagnetMode {
    INVALID((byte) -2), NO_CARD((byte) -1), OFF((byte) 0), PICKUP_INVENTORY((byte) 0b01), PICKUP_ME((byte) 0b10),
    PICKUP_ME_NO_MAGNET((byte) 0b11);

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

    public MagnetMode toggleMagnet() {
        if (id < 0)
            return this;
        return fromByte((byte) (id ^ 0x01));
    }

    public MagnetMode togglePickupME() {
        if (id < 0)
            return this;
        return fromByte((byte) (id ^ 0x10));
    }

    public static MagnetMode fromByte(byte b) {
        return switch (b) {
            case 1 -> PICKUP_INVENTORY;
            case 2 -> PICKUP_ME;
            case 3 -> PICKUP_ME_NO_MAGNET;
            default -> OFF;
        };
    }
}
