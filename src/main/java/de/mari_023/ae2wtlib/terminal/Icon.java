package de.mari_023.ae2wtlib.terminal;

import net.minecraft.resources.ResourceLocation;

import appeng.client.gui.style.Blitter;
import appeng.core.AppEng;

/**
 * Edit in {@code assets/ae2/textures/wtlib/guis/icons.png}.
 */
public record Icon(int x, int y, int width, int height, Texture texture) {

    public static final Texture TEXTURE = new Texture(AppEng.makeId("textures/wtlib/guis/icons.png"), 128, 128);
    public static final Texture AE2_TEXTURE = new Texture(appeng.client.gui.Icon.TEXTURE,
            appeng.client.gui.Icon.TEXTURE_WIDTH, appeng.client.gui.Icon.TEXTURE_HEIGHT);

    public static final Icon BUTTON_BACKGROUND = new Icon(240, 240, 16, 16, AE2_TEXTURE);

    public static final Icon MAGNET = new Icon(32, 64, 16, 16, AE2_TEXTURE);
    public static final Icon MAGNET_FILTER = new Icon(0, 0);
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
