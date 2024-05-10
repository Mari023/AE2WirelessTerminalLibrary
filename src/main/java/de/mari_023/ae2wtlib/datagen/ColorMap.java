package de.mari_023.ae2wtlib.datagen;

import appeng.api.util.AEColor;

public record ColorMap(AEColor color) {
    private static final int BLACK_VARIANT = 0xFF333334;//TODO convert images to 000000
    private static final int MEDIUM_VARIANT = 0xFF6E6E6E;//TODO convert images to 888888
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
        return fromArgb(0xFF, getRrgb(rgb), getGrgb(rgb), getBrgb(rgb));
    }

    public static int getRrgb(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    public static int getGrgb(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    public static int getBrgb(int rgb) {
        return rgb & 0xFF;
    }

    public static int fromArgb(int a, int r, int g, int b) {
        //return (r << 24) | (g << 16) | (b << 8) | a;
        return (a << 24) | (b << 16) | (g << 8) | r;
    }
}
