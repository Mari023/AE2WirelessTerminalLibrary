package de.mari_023.fabric.ae2wtlib.wct;

import appeng.api.config.ActionItems;
import appeng.client.gui.Icon;
import appeng.client.gui.me.items.ItemTerminalScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.StyleManager;
import appeng.client.gui.widgets.ActionButton;
import appeng.client.gui.widgets.IconButton;
import com.mojang.blaze3d.platform.GlStateManager;
import de.mari_023.fabric.ae2wtlib.ae2wtlibConfig;
import de.mari_023.fabric.ae2wtlib.mixin.ScreenMixin;
import de.mari_023.fabric.ae2wtlib.trinket.AppEngTrinketSlot;
import de.mari_023.fabric.ae2wtlib.trinket.TrinketInvRenderer;
import de.mari_023.fabric.ae2wtlib.util.ItemButton;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.MagnetMode;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.MagnetSettings;
import de.mari_023.fabric.ae2wtlib.wut.CycleTerminalButton;
import de.mari_023.fabric.ae2wtlib.wut.IUniversalTerminalCapable;
import dev.emi.trinkets.TrinketInventoryRenderer;
import dev.emi.trinkets.TrinketsClient;
import dev.emi.trinkets.api.SlotGroups;
import dev.emi.trinkets.api.TrinketSlots;
import dev.emi.trinkets.mixin.SlotMixin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WCTScreen extends ItemTerminalScreen<WCTContainer> implements IUniversalTerminalCapable {

    ItemButton magnetCardToggleButton;
    private List<AppEngTrinketSlot> trinketSlots;
    private float mouseX;
    private float mouseY;

    private static final ScreenStyle STYLE;

    static {
        ScreenStyle STYLE1;
        try {
            STYLE1 = StyleManager.loadStyleDoc("/screens/wtlib/wireless_crafting_terminal.json");
        } catch(IOException ignored) {
            STYLE1 = null;
        }
        STYLE = STYLE1;
    }

    public WCTScreen(WCTContainer container, PlayerInventory playerInventory, Text title) {
        super(container, playerInventory, title, STYLE);
        ActionButton clearBtn = new ActionButton(ActionItems.STASH, (btn) -> container.clearCraftingGrid());
        clearBtn.setHalfSize(true);
        widgets.add("clearCraftingGrid", clearBtn);
        IconButton deleteButton = new IconButton(btn -> delete()) {
            @Override
            protected Icon getIcon() {
                return Icon.CONDENSER_OUTPUT_TRASH;
            }
        };
        deleteButton.setHalfSize(true);
        deleteButton.setMessage(new TranslatableText("gui.ae2wtlib.emptytrash").append("\n").append(new TranslatableText("gui.ae2wtlib.emptytrash.desc")));
        widgets.add("emptyTrash", deleteButton);

        magnetCardToggleButton = new ItemButton(btn -> setMagnetMode(), new Identifier("ae2wtlib", "textures/magnet_card.png"));
        magnetCardToggleButton.setHalfSize(true);
        widgets.add("magnetCardToggleButton", magnetCardToggleButton);
        resetMagnetSettings();
        getScreenHandler().setScreen(this);

        if(getScreenHandler().isWUT()) widgets.add("cycleTerminal", new CycleTerminalButton(btn -> cycleTerminal()));

        widgets.add("player", new PlayerEntityWidget(MinecraftClient.getInstance().player));
    }

    @Override
    public void init() {
        super.init();
        if(!ae2wtlibConfig.INSTANCE.allowTrinket()) return;//Trinkets only
        TrinketsClient.displayEquipped = 0;
        trinketSlots = new ArrayList<>();
        for(Slot slot : getScreenHandler().slots) {
            if(!(slot instanceof AppEngTrinketSlot)) continue;
            AppEngTrinketSlot ts = (AppEngTrinketSlot) slot;
            trinketSlots.add(ts);
            if(!ts.keepVisible) ((SlotMixin) slot).setXPosition(Integer.MIN_VALUE);
            else {
                ((SlotMixin) ts).setXPosition(getGroupX(TrinketSlots.getSlotFromName(ts.group, ts.slot).getSlotGroup()) + 1);
                ((SlotMixin) ts).setYPosition(getGroupY(TrinketSlots.getSlotFromName(ts.group, ts.slot).getSlotGroup()) + 1);
            }
        }
    }

    @Override
    public void drawBG(MatrixStack matrices, final int offsetX, final int offsetY, final int mouseX, final int mouseY, float partialTicks) {
        super.drawBG(matrices, offsetX, offsetY, mouseX, mouseY, partialTicks);
        if(!ae2wtlibConfig.INSTANCE.allowTrinket()) return;//Trinkets only starting here
        GlStateManager.disableDepthTest();
        List<TrinketSlots.Slot> trinketSlots = TrinketSlots.getAllSlots();
        setZOffset(100);
        itemRenderer.zOffset = 100.0F;
        int trinketOffset = -1;
        for(int i = 55; i < handler.slots.size(); i++) {
            if(!(handler.slots.get(i) instanceof AppEngTrinketSlot)) continue;
            if(trinketOffset == -1) trinketOffset = i;
            Slot ts = handler.getSlot(i);
            TrinketSlots.Slot s = trinketSlots.get(i - trinketOffset);
            if(!s.getSlotGroup().onReal && s.getSlotGroup().slots.get(0) == s)
                renderSlotBack(matrices, ts, s, x, y);
        }
        setZOffset(0);
        itemRenderer.zOffset = 0.0F;
        GlStateManager.enableDepthTest();
        TrinketSlots.SlotGroup lastGroup = TrinketSlots.slotGroups.get(TrinketSlots.slotGroups.size() - 1);
        int lastX = getGroupX(lastGroup);
        int lastY = getGroupY(lastGroup);
        if(lastX < 0)
            TrinketInvRenderer.renderExcessSlotGroups(matrices, this, client.getTextureManager(), x, y + backgroundHeight - 167, lastX, lastY);
        for(TrinketSlots.SlotGroup group : TrinketSlots.slotGroups) {
            if(group.onReal && group.slots.size() > 0) continue;
            client.getTextureManager().bindTexture(TrinketInventoryRenderer.MORE_SLOTS_TEX);
            drawTexture(matrices, x + getGroupX(group), y + getGroupY(group), 4, 4, 18, 18);
        }
    }

    @Override
    public void drawFG(MatrixStack matrices, final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        super.drawFG(matrices, offsetX, offsetY, mouseX, mouseY);

        if(!ae2wtlibConfig.INSTANCE.allowTrinket() || client == null) return;//Trinkets client-only starting here
        if(TrinketsClient.slotGroup != null)
            TrinketInvRenderer.renderGroupFront(matrices, this, client.getTextureManager(), 0, 0, TrinketsClient.slotGroup, getGroupX(TrinketsClient.slotGroup), getGroupY(TrinketsClient.slotGroup));
        else if(TrinketsClient.displayEquipped > 0 && TrinketsClient.lastEquipped != null)
            TrinketInvRenderer.renderGroupFront(matrices, this, client.getTextureManager(), 0, 0, TrinketsClient.lastEquipped, getGroupX(TrinketsClient.lastEquipped), getGroupY(TrinketsClient.lastEquipped));

        GlStateManager.disableDepthTest();
        List<TrinketSlots.Slot> trinketSlots = TrinketSlots.getAllSlots();
        int trinketOffset = -1;
        for(int i = 0; i < handler.slots.size(); i++) {
            if(!(handler.slots.get(i) instanceof AppEngTrinketSlot)) continue;
            if(trinketOffset == -1) trinketOffset = i;
            Slot ts = handler.getSlot(i);
            TrinketSlots.Slot s = trinketSlots.get(i - trinketOffset);
            if(!(s.getSlotGroup() == TrinketsClient.slotGroup || !(s.getSlotGroup() == TrinketsClient.lastEquipped && TrinketsClient.displayEquipped > 0)))
                renderSlot(matrices, ts, s, mouseX, mouseY);
        }
        //Redraw only the active group slots so they're always on top
        trinketOffset = -1;
        for(int i = 0; i < handler.slots.size(); i++) {
            if(!(handler.slots.get(i) instanceof AppEngTrinketSlot)) continue;
            if(trinketOffset == -1) trinketOffset = i;
            Slot ts = handler.getSlot(i);
            TrinketSlots.Slot s = trinketSlots.get(i - trinketOffset);
            if(s.getSlotGroup() == TrinketsClient.slotGroup || (s.getSlotGroup() == TrinketsClient.lastEquipped && TrinketsClient.displayEquipped > 0))
                renderSlot(matrices, ts, s, mouseX, mouseY);
        }
        GlStateManager.enableDepthTest();
    }

    private void delete() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString("CraftingTerminal.Delete");
        buf.writeBoolean(false);
        ClientPlayNetworking.send(new Identifier("ae2wtlib", "general"), buf);
    }

    private MagnetSettings magnetSettings = null;

    public void resetMagnetSettings() {
        magnetSettings = getScreenHandler().getMagnetSettings();
        setMagnetModeText();
    }

    private void setMagnetMode() {
        switch(magnetSettings.magnetMode) {
            case INVALID:
            case NO_CARD:
                return;
            case OFF:
                magnetSettings.magnetMode = MagnetMode.PICKUP_INVENTORY;
                break;
            case PICKUP_INVENTORY:
                magnetSettings.magnetMode = MagnetMode.PICKUP_ME;
                break;
            case PICKUP_ME:
                magnetSettings.magnetMode = MagnetMode.OFF;
                break;
        }
        setMagnetModeText();
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString("CraftingTerminal.SetMagnetMode");
        buf.writeByte(magnetSettings.magnetMode.getId());
        ClientPlayNetworking.send(new Identifier("ae2wtlib", "general"), buf);
    }

    private void setMagnetModeText() {
        switch(magnetSettings.magnetMode) {
            case INVALID:
            case NO_CARD:
                magnetCardToggleButton.setVisibility(false);
                break;
            case OFF:
                magnetCardToggleButton.setVisibility(true);
                magnetCardToggleButton.setMessage(new TranslatableText("gui.ae2wtlib.magnetcard").append("\n").append(new TranslatableText("gui.ae2wtlib.magnetcard.desc.off")));
                break;
            case PICKUP_INVENTORY:
                magnetCardToggleButton.setVisibility(true);
                magnetCardToggleButton.setMessage(new TranslatableText("gui.ae2wtlib.magnetcard").append("\n").append(new TranslatableText("gui.ae2wtlib.magnetcard.desc.inv")));
                break;
            case PICKUP_ME:
                magnetCardToggleButton.setVisibility(true);
                magnetCardToggleButton.setMessage(new TranslatableText("gui.ae2wtlib.magnetcard").append("\n").append(new TranslatableText("gui.ae2wtlib.magnetcard.desc.me")));
                break;
        }
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.mouseX = (float) mouseX;
        this.mouseY = (float) mouseY;
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void tick() {
        super.tick();
        if(!ae2wtlibConfig.INSTANCE.allowTrinket()) return;
        float relX = mouseX - x;
        float relY = mouseY - y;
        if(TrinketsClient.slotGroup == null || !inBounds(TrinketsClient.slotGroup, relX, relY, true)) {
            if(TrinketsClient.slotGroup != null) for(AppEngTrinketSlot ts : trinketSlots)
                if(ts.group.equals(TrinketsClient.slotGroup.getName()) && !ts.keepVisible)
                    ((SlotMixin) ts).setXPosition(Integer.MIN_VALUE);
            TrinketsClient.slotGroup = null;
            for(TrinketSlots.SlotGroup group : TrinketSlots.slotGroups)
                if(inBounds(group, relX, relY, false) && group.slots.size() > 0) {
                    TrinketsClient.displayEquipped = 0;
                    TrinketsClient.slotGroup = group;
                    List<AppEngTrinketSlot> tSlots = new ArrayList<>();
                    for(AppEngTrinketSlot ts : trinketSlots) if(ts.group.equals(group.getName())) tSlots.add(ts);
                    int groupX = getGroupX(group);
                    int groupY = getGroupY(group);
                    int count = group.slots.size();
                    int offset = 1;
                    if(group.onReal) {
                        count++;
                        offset = 0;
                    } else {
                        ((SlotMixin) tSlots.get(0)).setXPosition(groupX + 1);
                        ((SlotMixin) tSlots.get(0)).setYPosition(groupY + 1);
                    }
                    int l = count / 2;
                    int r = count - l - 1;
                    if(tSlots.size() == 0) break;
                    for(int i = 0; i < l; i++) {
                        ((SlotMixin) tSlots.get(i + offset)).setXPosition(groupX - (i + 1) * 18 + 1);
                        ((SlotMixin) tSlots.get(i + offset)).setYPosition(groupY + 1);
                    }
                    for(int i = 0; i < r; i++) {
                        ((SlotMixin) tSlots.get(i + l + offset)).setXPosition(groupX + (i + 1) * 18 + 1);
                        ((SlotMixin) tSlots.get(i + l + offset)).setYPosition(groupY + 1);
                    }
                    TrinketsClient.activeSlots = new ArrayList<>();
                    if(group.vanillaSlot != -1) {
                        Slot slot = getScreenHandler().SlotsWithTrinket[group.vanillaSlot];
                        if(slot != null) TrinketsClient.activeSlots.add(slot);
                    }
                    TrinketsClient.activeSlots.addAll(tSlots);
                    break;
                }
        }
        if(TrinketsClient.displayEquipped > 0) {
            TrinketsClient.displayEquipped--;
            if(TrinketsClient.slotGroup == null) {
                TrinketSlots.SlotGroup group = TrinketsClient.lastEquipped;
                if(group != null) {
                    List<AppEngTrinketSlot> tSlots = new ArrayList<>();
                    for(AppEngTrinketSlot ts : trinketSlots) if(ts.group.equals(group.getName())) tSlots.add(ts);
                    int groupX = getGroupX(group);
                    int groupY = getGroupY(group);
                    int count = group.slots.size();
                    int offset = 1;
                    if(group.onReal) {
                        count++;
                        offset = 0;
                    } else {
                        ((SlotMixin) tSlots.get(0)).setXPosition(groupX + 1);
                        ((SlotMixin) tSlots.get(0)).setYPosition(groupY + 1);
                    }
                    int l = count / 2;
                    int r = count - l - 1;
                    for(int i = 0; i < l; i++) {
                        ((SlotMixin) tSlots.get(i + offset)).setXPosition(groupX - (i + 1) * 18 + 1);
                        ((SlotMixin) tSlots.get(i + offset)).setYPosition(groupY + 1);
                    }
                    for(int i = 0; i < r; i++) {
                        ((SlotMixin) tSlots.get(i + l + offset)).setXPosition(groupX + (i + 1) * 18 + 1);
                        ((SlotMixin) tSlots.get(i + l + offset)).setYPosition(groupY + 1);
                    }
                    TrinketsClient.activeSlots = new ArrayList<>();
                    if(group.vanillaSlot != -1)
                        TrinketsClient.activeSlots.add(getScreenHandler().getSlot(group.vanillaSlot));
                    TrinketsClient.activeSlots.addAll(tSlots);
                }
            }
        }
        for(AppEngTrinketSlot ts : trinketSlots) {
            if(((TrinketsClient.lastEquipped == null || TrinketsClient.displayEquipped <= 0 || !ts.group.equals(TrinketsClient.lastEquipped.getName()))
                    && (TrinketsClient.slotGroup == null || !ts.group.equals(TrinketsClient.slotGroup.getName()))) && !ts.keepVisible)
                ((SlotMixin) ts).setXPosition(Integer.MIN_VALUE);
        }
        for(AppEngTrinketSlot ts : trinketSlots) {
            int groupX = getGroupX(TrinketSlots.getSlotFromName(ts.group, ts.slot).getSlotGroup());
            if(ts.keepVisible && groupX < 0) ((SlotMixin) ts).setXPosition(groupX + 1);
        }
    }

    @Override
    protected boolean isClickOutsideBounds(double x, double y, int i, int j, int k) {
        if(ae2wtlibConfig.INSTANCE.allowTrinket() && TrinketsClient.slotGroup != null && inBounds(TrinketsClient.slotGroup, (float) x - this.x, (float) y - this.y, true))
            return false;
        return super.isClickOutsideBounds(x, y, i, j, k);
    }

    public boolean inBounds(TrinketSlots.SlotGroup group, float x, float y, boolean focused) {
        int groupX = getGroupX(group);
        int groupY = getGroupY(group);
        if(focused) {
            int count = group.slots.size();
            if(group.onReal) count++;
            int l = count / 2;
            int r = count - l - 1;
            return x > groupX - l * 18 - 4 && y > groupY - 4 && x < groupX + r * 18 + 22 && y < groupY + 22;
        } else return x > groupX && y > groupY && x < groupX + 18 && y < groupY + 18;
    }

    public int getGroupX(TrinketSlots.SlotGroup group) {
        if(group.vanillaSlot == 5) return 7;
        if(group.vanillaSlot == 6) return 7;
        if(group.vanillaSlot == 7) return 7;
        if(group.vanillaSlot == 8) return 7;
        if(group.vanillaSlot == 45) return 79;
        if(group.getName().equals(SlotGroups.HAND)) return 61;
        int j = 0;
        if(TrinketSlots.slotGroups.get(5).slots.size() == 0) j = -1;
        for(int i = 6; i < TrinketSlots.slotGroups.size(); i++) {
            if(TrinketSlots.slotGroups.get(i) == group) {
                j += i;
                return -15 - ((j - 5) / 4) * 18;
            } else if(TrinketSlots.slotGroups.get(i).slots.size() == 0) j--;
        }
        return 0;
    }

    public int getGroupY(TrinketSlots.SlotGroup group) {
        if(group.vanillaSlot == 5) return -161 + backgroundHeight;
        if(group.vanillaSlot == 6) return -143 + backgroundHeight;
        if(group.vanillaSlot == 7) return -125 + backgroundHeight;
        if(group.vanillaSlot == 8) return -107 + backgroundHeight;
        if(group.vanillaSlot == 45) return -107 + backgroundHeight;
        if(group.getName().equals(SlotGroups.HAND)) return -107 + backgroundHeight;
        int j = 0;
        if(TrinketSlots.slotGroups.get(5).slots.size() == 0) j = -1;
        for(int i = 6; i < TrinketSlots.slotGroups.size(); i++) {
            if(TrinketSlots.slotGroups.get(i) == group) {
                j += i;
                return ((j - 5) % 4) * 18 + backgroundHeight - 161;
            } else if(TrinketSlots.slotGroups.get(i).slots.size() == 0) j--;
        }
        return 0;
    }

    private static final Identifier BLANK_BACK = new Identifier("trinkets", "textures/gui/blank_back.png");

    public void renderSlotBack(MatrixStack matrices, Slot ts, TrinketSlots.Slot s, int x, int y) {
        assert client != null;
        GlStateManager.disableLighting();
        if(ts.getStack().isEmpty()) client.getTextureManager().bindTexture(s.texture);
        else client.getTextureManager().bindTexture(BLANK_BACK);
        DrawableHelper.drawTexture(matrices, x + ts.x, y + ts.y, 0, 0, 0, 16, 16, 16, 16);
        GlStateManager.enableLighting();
    }

    public void renderSlot(MatrixStack matrices, Slot ts, TrinketSlots.Slot s, int x, int y) {
        assert client != null;
        matrices.push();
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();
        if(ts.getStack().isEmpty()) client.getTextureManager().bindTexture(s.texture);
        else client.getTextureManager().bindTexture(BLANK_BACK);
        DrawableHelper.drawTexture(matrices, ts.x, ts.y, 0, 0, 0, 16, 16, 16, 16);
        ((ScreenMixin) this).invokeDrawSlot(matrices, ts);
        if(isPointOverSlot(ts, x, y) && ts.doDrawHoveringEffect()) {
            focusedSlot = ts;
            GlStateManager.colorMask(true, true, true, false);
            fillGradient(matrices, ts.x, ts.y, ts.x + 16, ts.y + 16, -2130706433, -2130706433);
            GlStateManager.colorMask(true, true, true, true);
        }
        matrices.pop();
    }

    private boolean isPointOverSlot(Slot slot, double a, double b) {
        if(TrinketsClient.slotGroup == null) {
            if(slot instanceof AppEngTrinketSlot) return false;
            return ((ScreenMixin) this).invokeIsPointOverSlot(slot, a, b);
        }
        if(TrinketsClient.activeSlots == null) return false;
        for(Slot s : TrinketsClient.activeSlots) {
            if(s == null) continue;
            if(s == slot) return isPointWithinBounds(slot.x, slot.y, 16, 16, a, b);
        }
        return false;
    }
}