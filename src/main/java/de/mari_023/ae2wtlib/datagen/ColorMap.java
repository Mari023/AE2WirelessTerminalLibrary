package de.mari_023.ae2wtlib.datagen;

import appeng.api.util.AEColor;

public record ColorMap(AEColor color) {
    private static final int blackVariant = 0xFF000000;
    private static final int mediumVariant = 0xFF888888;
    private static final int whiteVariant = 0xFFFFFFFF;

    public int map(int argb) {
        return switch (argb) {
            case blackVariant -> color().blackVariant;
            case mediumVariant -> color().mediumVariant;
            case whiteVariant -> color().whiteVariant;
            default -> argb;
        };
    }
}
