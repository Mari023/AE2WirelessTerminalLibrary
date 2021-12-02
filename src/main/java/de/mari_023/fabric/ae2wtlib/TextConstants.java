package de.mari_023.fabric.ae2wtlib;

import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public final class TextConstants {

    private TextConstants() {}

    public static final Style STYLE_RED = Style.EMPTY.withColor(Formatting.RED);
    public static final Style STYLE_GREEN = Style.EMPTY.withColor(Formatting.GREEN);
    public static final Style STYLE_GRAY = Style.EMPTY.withColor(Formatting.GRAY);


    public static final MutableText RESTOCK_ON = new TranslatableText("gui.ae2wtlib.restock").append(new TranslatableText("gui.ae2wtlib.on").setStyle(STYLE_GREEN));
    public static final MutableText RESTOCK_OFF = new TranslatableText("gui.ae2wtlib.restock").append(new TranslatableText("gui.ae2wtlib.off").setStyle(STYLE_RED));

    public static final MutableText HOTKEY_MAGNETCARD_INVENTORY = new TranslatableText("gui.ae2wtlib.magnetcard.hotkey").append(new TranslatableText("gui.ae2wtlib.magnetcard.desc.inv").setStyle(STYLE_GREEN));
    public static final MutableText HOTKEY_MAGNETCARD_ME = new TranslatableText("gui.ae2wtlib.magnetcard.hotkey").append(new TranslatableText("gui.ae2wtlib.magnetcard.desc.me").setStyle(STYLE_GREEN));
    public static final MutableText HOTKEY_MAGNETCARD_OFF = new TranslatableText("gui.ae2wtlib.magnetcard.hotkey").append(new TranslatableText("gui.ae2wtlib.magnetcard.desc.off").setStyle(STYLE_RED));
    public static final MutableText MAGNETCARD_OFF = new TranslatableText("gui.ae2wtlib.magnetcard").append("\n").append(new TranslatableText("gui.ae2wtlib.magnetcard.desc.off"));
    public static final MutableText MAGNETCARD_INVENTORY = new TranslatableText("gui.ae2wtlib.magnetcard").append("\n").append(new TranslatableText("gui.ae2wtlib.magnetcard.desc.inv"));
    public static final MutableText MAGNETCARD_ME = new TranslatableText("gui.ae2wtlib.magnetcard").append("\n").append(new TranslatableText("gui.ae2wtlib.magnetcard.desc.me"));
    public static final TranslatableText MAGNETCARD_TOOLTIP = new TranslatableText("item.ae2wtlib.magnet_card.desc");

    public static final MutableText UNIVERSAL = new TranslatableText("item.ae2wtlib.wireless_universal_terminal.desc").fillStyle(STYLE_GRAY);
    public static final MutableText CRAFTING = new TranslatableText("item.ae2.wireless_crafting_terminal").fillStyle(STYLE_GRAY);
    public static final MutableText PATTERN_ENCODING = new TranslatableText("item.ae2wtlib.wireless_pattern_encoding_terminal").fillStyle(STYLE_GRAY);
    public static final MutableText PATTERN_ACCESS = new TranslatableText("item.ae2wtlib.wireless_pattern_access_terminal").fillStyle(STYLE_GRAY);

    public static final LiteralText TERMINAL_EMPTY = new LiteralText("This terminal does not contain any other Terminals");

    public static final TranslatableText BOOSTER = new TranslatableText("item.ae2wtlib.infinity_booster_card.desc");

    public static final TranslatableText CYCLE = new TranslatableText("gui.ae2wtlib.cycle_terminal");
    public static final TranslatableText CYCLE_TOOLTIP = new TranslatableText("gui.ae2wtlib.cycle_terminal.desc");

    public static final MutableText DELETE = new TranslatableText("gui.ae2wtlib.emptytrash").append("\n").append(new TranslatableText("gui.ae2wtlib.emptytrash.desc"));
}
