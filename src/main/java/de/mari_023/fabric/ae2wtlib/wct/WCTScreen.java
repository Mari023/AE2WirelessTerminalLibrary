package de.mari_023.fabric.ae2wtlib.wct;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.client.gui.Icon;
import appeng.client.gui.me.items.CraftingTermScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.StyleManager;
import appeng.client.gui.widgets.IconButton;
import com.mojang.blaze3d.vertex.PoseStack;
import de.mari_023.fabric.ae2wtlib.TextConstants;
import de.mari_023.fabric.ae2wtlib.AE2wtlib;
import de.mari_023.fabric.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.fabric.ae2wtlib.util.ItemButton;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.MagnetMode;
import de.mari_023.fabric.ae2wtlib.wut.CycleTerminalButton;
import de.mari_023.fabric.ae2wtlib.wut.IUniversalTerminalCapable;
import dev.emi.trinkets.api.SlotGroup;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class WCTScreen extends CraftingTermScreen<WCTMenu> implements IUniversalTerminalCapable {

    ItemButton magnetCardToggleButton;
    private float mouseX;
    private float mouseY;

    private static final ScreenStyle STYLE;

    static {
        ScreenStyle STYLE1;
        try {
            STYLE1 = StyleManager.loadStyleDoc("/screens/wtlib/wireless_crafting_terminal.json");
        } catch(IOException e) {
            e.printStackTrace();
            STYLE1 = null;
        }
        STYLE = STYLE1;
    }

    private static final ResourceLocation MORE_SLOTS = new ResourceLocation("trinkets", "textures/gui/more_slots.png");
    private Rect2i currentBounds = new Rect2i(0, 0, 0, 0);
    private Rect2i typeBounds = new Rect2i(0, 0, 0, 0);
    private Rect2i quickMoveBounds = new Rect2i(0, 0, 0, 0);
    private Rect2i quickMoveTypeBounds = new Rect2i(0, 0, 0, 0);
    private SlotGroup group = null;
    private SlotGroup quickMoveGroup = null;

    public WCTScreen(WCTMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title, STYLE);
        IconButton deleteButton = new IconButton(btn -> getMenu().deleteTrashSlot()) {
            @Override
            protected Icon getIcon() {
                return Icon.CONDENSER_OUTPUT_TRASH;
            }
        };
        deleteButton.setHalfSize(true);
        deleteButton.setMessage(TextConstants.DELETE);
        widgets.add("emptyTrash", deleteButton);

        magnetCardToggleButton = new ItemButton(btn -> setMagnetMode(), new ResourceLocation(AE2wtlib.MOD_NAME, "textures/magnet_card.png"));
        widgets.add("magnetCardToggleButton", magnetCardToggleButton);

        if(getMenu().isWUT()) widgets.add("cycleTerminal", new CycleTerminalButton(btn -> cycleTerminal()));

        widgets.add("player", new PlayerEntityWidget(Minecraft.getInstance().player));
    }

    private void setMagnetMode() {
        switch(getMenu().getMagnetSettings().magnetMode) {
            case INVALID:
            case NO_CARD:
                return;
            case OFF:
                getMenu().setMagnetMode(MagnetMode.PICKUP_INVENTORY);
                break;
            case PICKUP_INVENTORY:
                getMenu().setMagnetMode(MagnetMode.PICKUP_ME);
                break;
            case PICKUP_ME:
                getMenu().setMagnetMode(MagnetMode.OFF);
                break;
        }
    }

    private void setMagnetModeText() {
        switch(getMenu().getMagnetSettings().magnetMode) {
            case INVALID, NO_CARD -> magnetCardToggleButton.setVisibility(false);
            case OFF -> {
                magnetCardToggleButton.setVisibility(true);
                magnetCardToggleButton.setMessage(TextConstants.MAGNETCARD_OFF);
            }
            case PICKUP_INVENTORY -> {
                magnetCardToggleButton.setVisibility(true);
                magnetCardToggleButton.setMessage(TextConstants.MAGNETCARD_INVENTORY);
            }
            case PICKUP_ME -> {
                magnetCardToggleButton.setVisibility(true);
                magnetCardToggleButton.setMessage(TextConstants.MAGNETCARD_ME);
            }
        }
    }

    protected void updateBeforeRender() {
        super.updateBeforeRender();
        setMagnetModeText();
    }

    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        try {
            super.render(matrices, mouseX, mouseY, delta);
        } catch(Exception ignored) {}
    }

    /*public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.mouseX = (float) mouseX;
        this.mouseY = (float) mouseY;
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void init() {
        super.init();
        if(!ae2wtlibConfig.INSTANCE.allowTrinket()) return;//Trinkets only
        handler.updateTrinketSlots(true);
        group = null;
        currentBounds = new Rect2i(0, 0, 0, 0);
    }

    private Rect2i getGroupRect(SlotGroup group) {
        Pair<Integer, Integer> pos = handler.getGroupPos(group);
        if(pos != null) {
            return new Rect2i(pos.getLeft() - 1, pos.getRight() - 1, 17, 17);
        }
        return new Rect2i(0, 0, 0, 0);
    }

    @Override
    public void drawBG(MatrixStack matrices, final int offsetX, final int offsetY, final int mouseX, final int mouseY, float partialTicks) {
        super.drawBG(matrices, offsetX, offsetY, mouseX, mouseY, partialTicks);
        if(!Config.allowTrinket()) return;//Trinkets only starting here
        int groupCount = handler.getGroupCount();
        if(groupCount <= 0) return;
        int width = groupCount / 4;
        int height = groupCount % 4;
        if(height == 0) {
            height = 4;
            width--;
        }
        RenderSystem.setShaderTexture(0, MORE_SLOTS);
        drawTexture(matrices, x + 3, y, 7, 26, 1, 7);
        // Repeated tops and bottoms
        for(int i = 0; i < width; i++) {
            drawTexture(matrices, x - 15 - 18 * i, y, 7, 26, 18, 7);
            drawTexture(matrices, x - 15 - 18 * i, y + 79, 7, 51, 18, 7);
        }
        // Top and bottom
        drawTexture(matrices, x - 15 - 18 * width, y, 7, 26, 18, 7);
        drawTexture(matrices, x - 15 - 18 * width, y + 7 + 18 * height, 7, 51, 18, 7);
        // Corners
        drawTexture(matrices, x - 22 - 18 * width, y, 0, 26, 7, 7);
        drawTexture(matrices, x - 22 - 18 * width, y + 7 + 18 * height, 0, 51, 7, 7);
        // Outer sides
        for(int i = 0; i < height; i++) {
            drawTexture(matrices, x - 22 - 18 * width, y + 7 + 18 * i, 0, 34, 7, 18);
        }
        // Inner sides
        if(width > 0) {
            for(int i = height; i < 4; i++) {
                drawTexture(matrices, x - 4 - 18 * width, y + 7 + 18 * i, 0, 34, 7, 18);
            }
        }
        if(width > 0 && height < 4) {
            // Bottom corner
            drawTexture(matrices, x - 4 - 18 * width, y + 79, 0, 51, 7, 7);
            // Inner corner
            drawTexture(matrices, x - 4 - 18 * width, y + 7 + 18 * height, 0, 58, 7, 7);
        }
        if(width > 0 || height == 4) {
            // Inner corner
            drawTexture(matrices, x, y + 79, 0, 58, 3, 7);
        }
    }

    @Override
    public void drawFG(MatrixStack matrices, final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        super.drawFG(matrices, offsetX, offsetY, mouseX, mouseY);

        if(!Config.allowTrinket()) return;//Trinkets client-only starting here
        if(TrinketsClient.activeGroup != null) {
            drawGroup(matrices, TrinketsClient.activeGroup, TrinketsClient.activeType);
        } else if(TrinketsClient.quickMoveGroup != null) {
            drawGroup(matrices, TrinketsClient.quickMoveGroup, TrinketsClient.quickMoveType);
        }
    }

    private void drawGroup(MatrixStack matrices, SlotGroup group, SlotType type) {
        RenderSystem.enableDepthTest();
        setZOffset(305);

        Pair<Integer, Integer> pos = handler.getGroupPos(group);
        int slotsWidth = handler.getSlotWidth(group) + 1;
        List<Pair<Integer, Integer>> slotHeights = handler.getSlotHeights(group);
        List<SlotType> slotTypes = handler.getSlotTypes(group);
        if(group.getSlotId() == -1) slotsWidth -= 1;
        int x = pos.getLeft() - 5 - (slotsWidth - 1) / 2 * 18;
        int y = pos.getRight() - 5;
        RenderSystem.setShaderTexture(0, MORE_SLOTS);

        if(slotsWidth > 1 || type != null) {
            drawTexture(matrices, x, y, 0, 0, 4, 26);

            for(int i = 0; i < slotsWidth; i++) {
                drawTexture(matrices, x + i * 18 + 4, y, 4, 0, 18, 26);
            }

            drawTexture(matrices, x + slotsWidth * 18 + 4, y, 22, 0, 4, 26);
            if(slotHeights != null) {
                for(int s = 0; s < slotHeights.size(); s++) {
                    if(slotTypes.get(s) != type) continue;
                    Pair<Integer, Integer> slotHeight = slotHeights.get(s);
                    int height = slotHeight.getRight();
                    if(height > 1) {
                        int top = (height - 1) / 2;
                        int bottom = height / 2;
                        int slotX = slotHeight.getLeft() - 5;
                        if(height > 2) {
                            drawTexture(matrices, slotX, y - top * 18, 0, 0, 26, 4);
                        }

                        for(int i = 1; i < top + 1; i++) {
                            drawTexture(matrices, slotX, y - i * 18 + 4, 0, 4, 26, 18);
                        }

                        for(int i = 1; i < bottom + 1; i++) {
                            drawTexture(matrices, slotX, y + i * 18 + 4, 0, 4, 26, 18);
                        }

                        drawTexture(matrices, slotX, y + 18 + bottom * 18 + 4, 0, 22, 26, 4);
                    }
                }

                // The rest of this is just to re-render a portion of the top and bottom slot borders so that corners
                // between slot types on the GUI look nicer
                for(int s = 0; s < slotHeights.size(); s++) {
                    Pair<Integer, Integer> slotHeight = slotHeights.get(s);
                    int height = slotHeight.getRight();
                    if(slotTypes.get(s) != type) height = 1;
                    int slotX = slotHeight.getLeft();
                    int top = (height - 1) / 2;
                    int bottom = height / 2;
                    drawTexture(matrices, slotX, y - top * 18 + 1, 4, 1, 16, 3);
                    drawTexture(matrices, slotX, y + (bottom + 1) * 18 + 4, 4, 22, 16, 3);
                }

                // Because pre-existing slots are not part of the slotHeights list
                if(group.getSlotId() != -1) {
                    drawTexture(matrices, pos.getLeft(), y + 1, 4, 1, 16, 3);
                    drawTexture(matrices, pos.getLeft(), y + 22, 4, 22, 14, 3);
                }
            }
        } else drawTexture(matrices, x + 4, y + 4, 4, 4, 18, 18);

        setZOffset(0);
        RenderSystem.disableDepthTest();
    }

    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
        if(!Config.allowTrinket()) return;
        if(group != null) {
            if(TrinketsClient.activeType != null) {
                if(!typeBounds.contains(Math.round(mouseX) - x, Math.round(mouseY) - y)) {
                    TrinketsClient.activeType = null;
                } else if(focusedSlot != null) {
                    if(!(focusedSlot instanceof AppEngTrinketSlot ts && ts.getType() == TrinketsClient.activeType)) {
                        TrinketsClient.activeType = null;
                    }
                }
            }
            if(TrinketsClient.activeType == null) {
                if(!currentBounds.contains(Math.round(mouseX) - x, Math.round(mouseY) - y)) {
                    TrinketsClient.activeGroup = null;
                    group = null;
                } else {
                    if(focusedSlot instanceof AppEngTrinketSlot ts) {
                        int i = handler.getSlotTypes(group).indexOf(ts.getType());
                        if(i >= 0) {
                            Pair<Integer, Integer> slotHeight = handler.getSlotHeights(group).get(i);
                            Rect2i r = getGroupRect(group);
                            int height = slotHeight.getRight();
                            if(height > 1) {
                                TrinketsClient.activeType = ts.getType();
                                typeBounds = new Rect2i(slotHeight.getLeft() - 3, r.getY() - (height - 1) / 2 * 18 - 3, 23, height * 18 + 5);
                            }
                        }
                    }
                }
            }
        }

        if(group == null && quickMoveGroup != null) {
            if(quickMoveTypeBounds.contains(Math.round(mouseX) - x, Math.round(mouseY) - y)) {
                TrinketsClient.activeGroup = quickMoveGroup;
                TrinketsClient.activeType = TrinketsClient.quickMoveType;
                int i = handler.getSlotTypes(TrinketsClient.activeGroup).indexOf(TrinketsClient.activeType);
                if(i >= 0) {
                    Pair<Integer, Integer> slotHeight = handler.getSlotHeights(TrinketsClient.activeGroup).get(i);
                    Rect2i r = getGroupRect(TrinketsClient.activeGroup);
                    int height = slotHeight.getRight();
                    if(height > 1) {
                        typeBounds = new Rect2i(slotHeight.getLeft() - 3, r.getY() - (height - 1) / 2 * 18 - 3, 23, height * 18 + 5);
                    }
                }
                TrinketsClient.quickMoveGroup = null;
            } else if(quickMoveBounds.contains(Math.round(mouseX) - x, Math.round(mouseY) - y)) {
                TrinketsClient.activeGroup = quickMoveGroup;
                TrinketsClient.quickMoveGroup = null;
            }
        }

        if(group == null) {
            for(SlotGroup g : TrinketsApi.getPlayerSlots().values()) {
                Rect2i r = getGroupRect(g);
                if(r.getX() < 0) continue;
                if(r.contains(Math.round(mouseX) - x, Math.round(mouseY) - y)) {
                    TrinketsClient.activeGroup = g;
                    TrinketsClient.quickMoveGroup = null;
                    break;
                }
            }
        }

        if(group != TrinketsClient.activeGroup) {
            group = TrinketsClient.activeGroup;

            if(group != null) {
                int slotsWidth = handler.getSlotWidth(group) + 1;
                if(group.getSlotId() == -1) slotsWidth -= 1;
                Rect2i r = getGroupRect(group);
                currentBounds = new Rect2i(0, 0, 0, 0);

                int l = (slotsWidth - 1) / 2 * 18;

                if(slotsWidth > 1) {
                    currentBounds = new Rect2i(r.getX() - l - 3, r.getY() - 3, slotsWidth * 18 + 5, 23);
                } else {
                    currentBounds = r;
                }

                if(focusedSlot instanceof AppEngTrinketSlot ts) {
                    int i = handler.getSlotTypes(group).indexOf(ts.getType());
                    if(i >= 0) {
                        Pair<Integer, Integer> slotHeight = handler.getSlotHeights(group).get(i);
                        int height = slotHeight.getRight();
                        if(height > 1) {
                            TrinketsClient.activeType = ts.getType();
                            typeBounds = new Rect2i(slotHeight.getLeft() - 3, r.getY() - (height - 1) / 2 * 18 - 3, 23, height * 18 + 5);
                        }
                    }
                }
            }
        }

        if(quickMoveGroup != TrinketsClient.quickMoveGroup) {
            quickMoveGroup = TrinketsClient.quickMoveGroup;

            if(quickMoveGroup != null) {
                int slotsWidth = handler.getSlotWidth(quickMoveGroup) + 1;

                if(quickMoveGroup.getSlotId() == -1) slotsWidth -= 1;
                Rect2i r = getGroupRect(quickMoveGroup);
                quickMoveBounds = new Rect2i(0, 0, 0, 0);

                int l = (slotsWidth - 1) / 2 * 18;
                quickMoveBounds = new Rect2i(r.getX() - l - 5, r.getY() - 5, slotsWidth * 18 + 8, 26);
                if(TrinketsClient.quickMoveType != null) {
                    int i = handler.getSlotTypes(quickMoveGroup).indexOf(TrinketsClient.quickMoveType);
                    if(i >= 0) {
                        Pair<Integer, Integer> slotHeight = handler.getSlotHeights(quickMoveGroup).get(i);
                        int height = slotHeight.getRight();
                        quickMoveTypeBounds = new Rect2i(slotHeight.getLeft() - 4, r.getY() - (height - 1) / 2 * 18 - 4, 26, height * 18 + 8);
                    }
                }
            }
        }

        if(TrinketsClient.quickMoveTimer > 0) {
            TrinketsClient.quickMoveTimer--;
            if(TrinketsClient.quickMoveTimer <= 0) TrinketsClient.quickMoveGroup = null;
        }
    }

    @Override
    protected boolean isClickOutsideBounds(double x, double y, int i, int j, int k) {
        if(ae2wtlibConfig.INSTANCE.allowTrinket() && isClickTrinketsBounds(x, y)) return false;
        return super.isClickOutsideBounds(x, y, i, j, k);
    }

    private boolean isClickTrinketsBounds(double mouseX, double mouseY) {
        int mx = (int) (Math.round(mouseX) - x);
        int my = (int) (Math.round(mouseY) - y);
        if(currentBounds.contains(mx, my)) return true;
        int groupCount = handler.getGroupCount();
        if(groupCount <= 0) return false;
        int width = groupCount / 4;
        int height = groupCount % 4;
        if(width > 0 && new Rect2i(-4 - 18 * width, 0, 7 + 18 * width, 86).contains(mx, my)) return true;
        return height > 0 && new Rect2i(-22 - 18 * width, 0, 25, 14 + 18 * height).contains(mx, my);
    }*/
}