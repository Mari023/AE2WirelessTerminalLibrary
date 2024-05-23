package de.mari_023.ae2wtlib.datagen;

import appeng.api.util.AEColor;

public record ColorMap(AEColor color) {
    private static final int BLACK_VARIANT = 0xFF000000;
    private static final int MEDIUM_VARIANT = 0xFF888888;
    private static final int WHITE_VARIANT = 0xFFFFFFFF;

    public int map(int argb) {
        return switch (argb) {
            case BLACK_VARIANT -> rgbToABGR(color().blackVariant);
            case MEDIUM_VARIANT -> rgbToABGR(color().mediumVariant);
            case WHITE_VARIANT -> rgbToABGR(color().whiteVariant);
            default -> argb;
        };
    }

    private static int rgbToABGR(int rgb) {
        return (0xFF << 24) | ((rgb & 0xFF) << 16) | (((rgb >> 8) & 0xFF) << 8) | (rgb >> 16) & 0xFF;
    }
}
