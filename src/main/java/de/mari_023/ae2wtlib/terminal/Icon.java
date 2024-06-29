package de.mari_023.ae2wtlib.terminal;

import net.minecraft.resources.ResourceLocation;

import appeng.client.gui.style.Blitter;
import appeng.core.AppEng;

/**
 * Edit in {@code assets/ae2/textures/wtlib/guis/icons.png}.
 */
public record Icon(int x, int y, int width, int height, Texture texture) {

    public static final Texture TEXTURE = new Texture(AppEng.makeId("textures/wtlib/guis/icons.png"), 128, 128);
    public static final Texture AE2TEXTURE = new Texture(appeng.client.gui.Icon.TEXTURE,
            appeng.client.gui.Icon.TEXTURE_WIDTH, appeng.client.gui.Icon.TEXTURE_HEIGHT);

    public static final Icon BUTTON_BACKGROUND = new Icon(79, 0, 16, 17);
    public static final Icon BUTTON_BACKGROUND_HOVER = new Icon(95, 1, 16, 16);
    public static final Icon BUTTON_BACKGROUND_SELECT = new Icon(95, 1, 16, 16);

    public static final Icon TOOLBAR_BUTTON_BACKGROUND = new Icon(176, 128, 18, 20, AE2TEXTURE);
    public static final Icon TOOLBAR_BUTTON_BACKGROUND_HOVER = new Icon(194, 128, 18, 20, AE2TEXTURE);
    public static final Icon TOOLBAR_BUTTON_BACKGROUND_SELECT = new Icon(212, 128, 18, 19, AE2TEXTURE);

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

    public static final Icon UPGRADE_BACKGROUND_TOP = new Icon(82, 62, 23, 23);
    public static final Icon UPGRADE_BACKGROUND_MIDDLE = new Icon(82, 85, 23, 18);
    public static final Icon UPGRADE_BACKGROUND_BOTTOM = new Icon(82, 103, 23, 25);
    public static final Icon UPGRADE_BACKGROUND_SCROLLING_TOP = new Icon(53, 62, 29, 23);
    public static final Icon UPGRADE_BACKGROUND_SCROLLING_MIDDLE = new Icon(53, 85, 29, 18);
    public static final Icon UPGRADE_BACKGROUND_SCROLLING_BOTTOM = new Icon(53, 103, 29, 25);

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
