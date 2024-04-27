package de.mari_023.ae2wtlib.wct.magnet_card;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public enum MagnetMode {
    INVALID((byte) -2), NO_CARD((byte) -1), OFF((byte) 0), PICKUP_INVENTORY((byte) 1), PICKUP_ME((byte) 2);

    public static final Codec<MagnetMode> CODEC = RecordCodecBuilder.<MagnetMode>mapCodec(
            builder -> builder
                    .group(Codec.BYTE.fieldOf("").forGetter(MagnetMode::getId))
                    .apply(builder, MagnetMode::fromByte))
            .codec();

    private final byte id;

    MagnetMode(byte b) {
        id = b;
    }

    public byte getId() {
        return id;
    }

    public boolean isDisabled() {
        return this != MagnetMode.PICKUP_INVENTORY && this != MagnetMode.PICKUP_ME;
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
