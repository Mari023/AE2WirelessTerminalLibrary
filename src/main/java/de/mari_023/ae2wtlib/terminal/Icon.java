package de.mari_023.ae2wtlib.terminal;

import net.minecraft.resources.ResourceLocation;

import appeng.client.gui.style.Blitter;
import appeng.core.AppEng;

/**
 * Edit in {@code assets/ae2/textures/wtlib/guis/icons.png}.
 */
public record Icon(int x, int y, int width, int height, Texture texture) {

    public static final Texture TEXTURE = new Texture(AppEng.makeId("textures/wtlib/guis/icons.png"), 256, 256);
    public static final Texture AE2TEXTURE = new Texture(appeng.client.gui.Icon.TEXTURE,
            appeng.client.gui.Icon.TEXTURE_WIDTH, appeng.client.gui.Icon.TEXTURE_HEIGHT);

    public static final Icon BUTTON_BACKGROUND = new Icon(63, 0, 16, 17);
    public static final Icon BUTTON_BACKGROUND_HOVERED = new Icon(95, 1, 16, 16);
    public static final Icon BUTTON_BACKGROUND_FOCUSED = new Icon(79, 0, 16, 17);

    public static final Icon TOOLBAR_BUTTON_BACKGROUND = new Icon(176, 128, 18, 20, AE2TEXTURE);
    public static final Icon TOOLBAR_BUTTON_BACKGROUND_HOVERED = new Icon(212, 128, 18, 19, AE2TEXTURE);
    public static final Icon TOOLBAR_BUTTON_BACKGROUND_FOCUSED = new Icon(194, 128, 18, 20, AE2TEXTURE);

    public static final Icon MAGNET = new Icon(0, 0);
    public static final Icon MAGNET_FILTER = new Icon(0, 16);
    public static final Icon TRASH = new Icon(0, 32);

    public static final Icon PATTERN_ACCESS = new Icon(16, 0);
    public static final Icon PATTERN_ENCODING = new Icon(16, 16);
    public static final Icon CRAFTING = new Icon(16, 32);

    public static final Icon NO = new Icon(32, 0);
    public static final Icon YES = new Icon(32, 16);
    public static final Icon UP = new Icon(32, 32);
    public static final Icon DOWN = new Icon(32, 48);
    public static final Icon SWITCH = new Icon(32, 64);

    public static final Icon EMPTY_ARMOR_SLOT_HELMET = new Icon(112, 0);
    public static final Icon EMPTY_ARMOR_SLOT_CHESTPLATE = new Icon(112, 16);
    public static final Icon EMPTY_ARMOR_SLOT_LEGGINGS = new Icon(112, 32);
    public static final Icon EMPTY_ARMOR_SLOT_BOOTS = new Icon(112, 48);
    public static final Icon EMPTY_ARMOR_SLOT_SHIELD = new Icon(112, 64);

    public static final Icon UPGRADE_BACKGROUND_TOP = new Icon(162, 0, 28, 23);
    public static final Icon UPGRADE_BACKGROUND_MIDDLE = new Icon(162, 23, 28, 18);
    public static final Icon UPGRADE_BACKGROUND_BOTTOM = new Icon(162, 41, 28, 25);
    public static final Icon UPGRADE_BACKGROUND_SCROLLING_TOP = new Icon(128, 0, 34, 23);
    public static final Icon UPGRADE_BACKGROUND_SCROLLING_MIDDLE = new Icon(128, 23, 34, 18);
    public static final Icon UPGRADE_BACKGROUND_SCROLLING_BOTTOM = new Icon(128, 41, 34, 25);

    public static final Icon SINGULARITY_BACKGROUND = new Icon(105, 98, 23, 30);

    private Icon(int x, int y) {
        this(x, y, 16, 16);
    }

    private Icon(int x, int y, int width, int height) {
        this(x, y, width, height, TEXTURE);
    }

    public Blitter getBlitter() {
        return Blitter.texture(texture.location(), texture.width(), texture.height())
                .src(x, y, width, height);
    }
    public record Texture(ResourceLocation location, int width, int height) {
    }
}
