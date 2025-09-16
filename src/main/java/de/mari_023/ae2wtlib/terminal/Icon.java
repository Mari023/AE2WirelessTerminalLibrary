package de.mari_023.ae2wtlib.terminal;

import net.minecraft.resources.ResourceLocation;

import appeng.client.gui.style.Blitter;
import appeng.core.AppEng;

/**
 * Edit in {@code assets/ae2/textures/wtlib/guis/icons.png}.
 */
public record Icon(int x, int y, int width, int height, Texture texture) {

    public static final Texture TEXTURE = new Texture(AppEng.makeId("textures/wtlib/guis/icons.png"), 128, 128);

    public static final Icon BUTTON_BACKGROUND = new Icon(63, 0, 16, 17);
    public static final Icon BUTTON_BACKGROUND_HOVERED = new Icon(95, 1, 16, 16);
    public static final Icon BUTTON_BACKGROUND_FOCUSED = new Icon(79, 0, 16, 17);

    public static final Icon MAGNET = new Icon(0, 0);
    public static final Icon MAGNET_FILTER = new Icon(0, 16);
    public static final Icon TRASH = new Icon(0, 32);

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
