package de.mari_023.ae2wtlib.datagen;

import static de.mari_023.ae2wtlib.datagen.TextureHelper.*;

import java.io.IOException;

import com.mojang.blaze3d.platform.NativeImage;

public class Coloramp implements IColoramp {
    private final int[] colors = new int[256];
    private final int meanRGB;

    public Coloramp(TextureManager mtm, int meanRGB, String name) {
        this.meanRGB = meanRGB;

        var gradientMapPath = "modern_industrialization:textures/gradient_maps/" + name + ".png";

        if (mtm.hasAsset(gradientMapPath)) {
            try (NativeImage gradientMap = mtm.getAssetAsTexture(gradientMapPath)) {
                for (int i = 0; i < 256; i++) {
                    int color = gradientMap.getPixelRGBA(i, 0);
                    int r = getR(color);
                    int g = getG(color);
                    int b = getB(color);
                    colors[i] = r << 16 | g << 8 | b;

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            fillUniform();
        }
    }

    public Coloramp(int meanRGB) {
        this.meanRGB = meanRGB;
        fillUniform();
    }

    private void fillUniform() {
        // Use uniform coloramp
        int meanR = TextureHelper.getRrgb(meanRGB);
        int meanG = TextureHelper.getGrgb(meanRGB);
        int meanB = TextureHelper.getBrgb(meanRGB);

        for (int i = 0; i < 256; ++i) {
            colors[i] = TextureHelper.toRGB(meanR * i / 255, meanG * i / 255, meanB * i / 255);
        }
    }

    @Override
    public int getRGB(double luminance) {
        int i = (int) (luminance * 255);
        return colors[i];
    }

    @Override
    public int getMeanRGB() {
        return meanRGB;
    }
}
