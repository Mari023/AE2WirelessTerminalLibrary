package de.mari_023.ae2wtlib.terminal;

public record UpgradeBackground(Icon top, Icon middle, Icon bottom) {

    private static final UpgradeBackground SCROLLING = new UpgradeBackground(Icon.UPGRADE_BACKGROUND_SCROLLING_TOP,
            Icon.UPGRADE_BACKGROUND_SCROLLING_MIDDLE, Icon.UPGRADE_BACKGROUND_SCROLLING_BOTTOM);
    private static final UpgradeBackground FIXED = new UpgradeBackground(Icon.UPGRADE_BACKGROUND_TOP,
            Icon.UPGRADE_BACKGROUND_MIDDLE, Icon.UPGRADE_BACKGROUND_BOTTOM);
    public static UpgradeBackground get(boolean scrolling) {
        return scrolling ? SCROLLING : FIXED;
    }
}
