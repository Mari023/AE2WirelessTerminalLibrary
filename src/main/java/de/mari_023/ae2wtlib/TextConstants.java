package de.mari_023.ae2wtlib;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import appeng.api.config.IncludeExclude;

public final class TextConstants {
    private TextConstants() {
    }

    public static final Style STYLE_RED = Style.EMPTY.withColor(ChatFormatting.RED);
    public static final Style STYLE_GREEN = Style.EMPTY.withColor(ChatFormatting.GREEN);
    public static final Style STYLE_GRAY = Style.EMPTY.withColor(ChatFormatting.GRAY);

    public static final MutableComponent RESTOCK_ON = Component.translatable("gui.ae2wtlib.restock")
            .append(Component.translatable("gui.ae2wtlib.on").setStyle(STYLE_GREEN));
    public static final MutableComponent RESTOCK_OFF = Component.translatable("gui.ae2wtlib.restock")
            .append(Component.translatable("gui.ae2wtlib.off").setStyle(STYLE_RED));

    public static final MutableComponent HOTKEY_MAGNETCARD_INVENTORY = Component
            .translatable("gui.ae2wtlib.magnetcard.hotkey")
            .append(Component.translatable("gui.ae2wtlib.magnetcard.desc.inv").setStyle(STYLE_GREEN));
    public static final MutableComponent HOTKEY_MAGNETCARD_ME = Component.translatable("gui.ae2wtlib.magnetcard.hotkey")
            .append(Component.translatable("gui.ae2wtlib.magnetcard.desc.me").setStyle(STYLE_GREEN));
    public static final MutableComponent HOTKEY_MAGNETCARD_OFF = Component
            .translatable("gui.ae2wtlib.magnetcard.hotkey")
            .append(Component.translatable("gui.ae2wtlib.magnetcard.desc.off").setStyle(STYLE_RED));
    public static final MutableComponent MAGNETCARD_OFF = Component.translatable("gui.ae2wtlib.magnetcard").append("\n")
            .append(Component.translatable("gui.ae2wtlib.magnetcard.desc.off"));
    public static final MutableComponent MAGNETCARD_INVENTORY = Component.translatable("gui.ae2wtlib.magnetcard")
            .append("\n").append(Component.translatable("gui.ae2wtlib.magnetcard.desc.inv"));
    public static final MutableComponent MAGNETCARD_ME = Component.translatable("gui.ae2wtlib.magnetcard").append("\n")
            .append(Component.translatable("gui.ae2wtlib.magnetcard.desc.me"));

    public static final MutableComponent UNIVERSAL = Component
            .translatable("item.ae2wtlib.wireless_universal_terminal.desc").withStyle(STYLE_GRAY);

    public static MutableComponent formatTerminalName(String terminal) {
        return Component.translatable(terminal).withStyle(STYLE_GRAY);
    }

    public static final Component TERMINAL_EMPTY = Component
            .literal("This terminal does not contain any other Terminals");
    public static final Component CYCLE_TOOLTIP = Component.translatable("gui.ae2wtlib.cycle_terminal.desc")
            .append("\n").append(Component.translatable("gui.ae2wtlib.cycle_terminal.desc1"));

    public static final MutableComponent TRASH = Component.translatable("gui.ae2wtlib.trash");

    public static final Component MAGNET_FILTER = Component.translatable("gui.ae2wtlib.Magnet");

    private static final Component ALLOW = Component.translatable("gui.ae2wtlib.whitelist");
    private static final Component DENY = Component.translatable("gui.ae2wtlib.blacklist");

    private static final Component PICKUP_FILTER = Component.translatable("gui.ae2wtlib.pickup_filter");
    private static final Component INSERT_FILTER = Component.translatable("gui.ae2wtlib.insert_filter");

    public static final MutableComponent PICKUP_ALLOW = PICKUP_FILTER.copy().append(ALLOW);
    public static final MutableComponent PICKUP_DENY = PICKUP_FILTER.copy().append(DENY);
    public static final MutableComponent INSERT_ALLOW = INSERT_FILTER.copy().append(ALLOW);
    public static final MutableComponent INSERT_DENY = INSERT_FILTER.copy().append(DENY);

    public static final MutableComponent COPY_PICKUP = Component.translatable("gui.ae2wtlib.copy_pickup");
    public static final MutableComponent COPY_INSERT = Component.translatable("gui.ae2wtlib.copy_insert");

    public static MutableComponent getPickupMode(IncludeExclude includeExclude) {
        return switch (includeExclude) {
            case WHITELIST -> PICKUP_ALLOW;
            case BLACKLIST -> PICKUP_DENY;
        };
    }

    public static MutableComponent getInsertMode(IncludeExclude includeExclude) {
        return switch (includeExclude) {
            case WHITELIST -> INSERT_ALLOW;
            case BLACKLIST -> INSERT_DENY;
        };
    }

    public static final Component SWITCH = Component.translatable("gui.ae2wtlib.switch");

    public static final Component CREATIVE_TAB = Component.translatable("gui.ae2wtlib.creativetab");

    public static final Component NETWORK_NOT_POWERED = Component.translatable("chat.ae2wtlib.NetworkNotPowered");
    public static final Component SINGULARITY_NOT_PRESENT = Component
            .translatable("chat.ae2wtlib.SingularityNotPresent");
    public static final Component NO_QNB_UPGRADE = Component.translatable("chat.ae2wtlib.NoQuantumBridgeCard");
    public static final Component NO_QNB = Component.translatable("chat.ae2wtlib.NoQuantumBridge");
    public static final Component DIFFERENT_NETWORKS = Component.translatable("chat.ae2wtlib.NetworkMismatch");
}
