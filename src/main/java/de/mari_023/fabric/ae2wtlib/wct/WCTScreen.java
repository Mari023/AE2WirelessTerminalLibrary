package de.mari_023.fabric.ae2wtlib.wct;

import appeng.api.config.ActionItems;
import appeng.client.gui.implementations.MEMonitorableScreen;
import appeng.client.gui.widgets.AETextField;
import appeng.client.gui.widgets.ActionButton;
import appeng.client.gui.widgets.IconButton;
import appeng.container.slot.CraftingMatrixSlot;
import appeng.core.localization.GuiText;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import com.mojang.blaze3d.systems.RenderSystem;
import de.mari_023.fabric.ae2wtlib.Config;
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
import me.shedaniel.math.Rectangle;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class WCTScreen extends MEMonitorableScreen<WCTContainer> implements IUniversalTerminalCapable {

    private int rows = 0;
    private AETextField searchField;
    private final int reservedSpace;
    ItemButton magnetCardToggleButton;
    private final WCTContainer container;
    private List<AppEngTrinketSlot> trinketSlots;
    private float mouseX;
    private float mouseY;

    public WCTScreen(WCTContainer container, PlayerInventory playerInventory, Text title) {
        super(container, playerInventory, title);
        reservedSpace = 73;
        this.container = container;

        try {
            Field f = MEMonitorableScreen.class.getDeclaredField("reservedSpace");
            f.setAccessible(true);
            f.set(this, reservedSpace);
            f.setAccessible(false);
        } catch(IllegalAccessException | NoSuchFieldException ignored) {}
    }

    @Override
    public void init() {
        super.init();
        ActionButton clearBtn = addButton(new ActionButton(x + 92 + 43, y + backgroundHeight - 156 - 4, ActionItems.STASH, btn -> clear()));
        clearBtn.setHalfSize(true);

        IconButton deleteButton = addButton(new IconButton(x + 92 + 25, y + backgroundHeight - 156 + 52, btn -> delete()) {
            @Override
            protected int getIconIndex() {
                return 6;
            }
        });
        deleteButton.setHalfSize(true);
        deleteButton.setMessage(new TranslatableText("gui.ae2wtlib.emptytrash").append("\n").append(new TranslatableText("gui.ae2wtlib.emptytrash.desc")));

        magnetCardToggleButton = addButton(new ItemButton(x + 92 + 60, y + backgroundHeight - 114, btn -> setMagnetMode(), new Identifier("ae2wtlib", "textures/magnet_card.png")));
        magnetCardToggleButton.setHalfSize(true);
        resetMagnetSettings();
        container.setScreen(this);

        if(container.isWUT()) addButton(new CycleTerminalButton(x - 18, y + 108, btn -> cycleTerminal()));

        try {
            Field field = MEMonitorableScreen.class.getDeclaredField("rows");
            field.setAccessible(true);
            Object value = field.get(this);
            field.setAccessible(false);
            rows = (int) value;
        } catch(IllegalAccessException | NoSuchFieldException ignored) {}
        try {
            Field field = MEMonitorableScreen.class.getDeclaredField("searchField");
            field.setAccessible(true);
            Object value = field.get(this);
            field.setAccessible(false);
            searchField = (AETextField) value;
        } catch(IllegalAccessException | NoSuchFieldException ignored) {}

        if(Config.allowTrinket()) {
            TrinketsClient.displayEquipped = 0;
            trinketSlots = new ArrayList<>();
            for(Slot slot : getScreenHandler().slots)
                if(slot instanceof AppEngTrinketSlot) {
                    AppEngTrinketSlot ts = (AppEngTrinketSlot) slot;
                    trinketSlots.add(ts);
                    if(!ts.keepVisible) ((SlotMixin) slot).setXPosition(Integer.MIN_VALUE);
                    else {
                        ((SlotMixin) ts).setXPosition(getGroupX(TrinketSlots.getSlotFromName(ts.group, ts.slot).getSlotGroup()) + 1);
                        ((SlotMixin) ts).setYPosition(getGroupY(TrinketSlots.getSlotFromName(ts.group, ts.slot).getSlotGroup()) + 1);
                    }
                }
        }
    }

    @Override
    public void drawBG(MatrixStack matrices, final int offsetX, final int offsetY, final int mouseX, final int mouseY, float partialTicks) {
        super.drawBG(matrices, offsetX, offsetY, mouseX, mouseY, partialTicks);
        bindTexture(getBackground());
        final int x_width = 197;
        drawTexture(matrices, offsetX, offsetY, 0, 0, x_width, 18);

        for(int x = 0; x < rows; x++) drawTexture(matrices, offsetX, offsetY + 18 + x * 18, 0, 18, x_width, 18);

        drawTexture(matrices, offsetX, offsetY + 16 + rows * 18, 0, 106 - 18 - 18, x_width, 99 + reservedSpace);

        searchField.render(matrices, mouseX, mouseY, partialTicks);

        if(client != null && client.player != null)
            drawEntity(offsetX + 52, offsetY + 94 + rows * 18, 30, (float) (offsetX + 52) - mouseX, (float) offsetY + 55 + rows * 18 - mouseY, client.player);

        if(Config.allowTrinket()) {
            RenderSystem.disableDepthTest();
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
            RenderSystem.enableDepthTest();
            TrinketSlots.SlotGroup lastGroup = TrinketSlots.slotGroups.get(TrinketSlots.slotGroups.size() - 1);
            int lastX = getGroupX(lastGroup);
            int lastY = getGroupY(lastGroup);
            if(lastX < 0)
                TrinketInvRenderer.renderExcessSlotGroups(matrices, this, client.getTextureManager(), x, y + backgroundHeight, lastX, lastY);
            for(TrinketSlots.SlotGroup group : TrinketSlots.slotGroups)
                if(!group.onReal && group.slots.size() > 0) {
                    client.getTextureManager().bindTexture(TrinketInventoryRenderer.MORE_SLOTS_TEX);
                    drawTexture(matrices, x + getGroupX(group), y + getGroupY(group), 4, 4, 18, 18);
                }
        }
    }

    @Override
    public void drawFG(MatrixStack matrices, final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        super.drawFG(matrices, offsetX, offsetY, mouseX, mouseY);
        textRenderer.draw(matrices, GuiText.CraftingTerminal.text(), 8, backgroundHeight - 96 + 1 - reservedSpace, 4210752);
        if(Config.allowTrinket() && client != null) {
            if(TrinketsClient.slotGroup != null)
                TrinketInvRenderer.renderGroupFront(matrices, this, client.getTextureManager(), 0, 0, TrinketsClient.slotGroup, getGroupX(TrinketsClient.slotGroup), getGroupY(TrinketsClient.slotGroup));
            else if(TrinketsClient.displayEquipped > 0 && TrinketsClient.lastEquipped != null)
                TrinketInvRenderer.renderGroupFront(matrices, this, client.getTextureManager(), 0, 0, TrinketsClient.lastEquipped, getGroupX(TrinketsClient.lastEquipped), getGroupY(TrinketsClient.lastEquipped));

            RenderSystem.disableDepthTest();
            List<TrinketSlots.Slot> trinketSlots = TrinketSlots.getAllSlots();
            int trinketOffset = -1;
            for(int i = 46; i < handler.slots.size(); i++) {
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
            RenderSystem.enableDepthTest();
        }
    }

    private void clear() {
        Slot s = null;
        for(final Slot j : handler.slots) if(j instanceof CraftingMatrixSlot) s = j;

        if(s != null) {
            final InventoryActionPacket p = new InventoryActionPacket(InventoryAction.MOVE_REGION, s.id, 0);
            NetworkHandler.instance().sendToServer(p);
        }
    }

    private void delete() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString("CraftingTerminal.Delete");
        buf.writeBoolean(false);
        ClientPlayNetworking.send(new Identifier("ae2wtlib", "general"), buf);
    }

    private MagnetSettings magnetSettings = null;

    public void resetMagnetSettings() {
        magnetSettings = container.getMagnetSettings();
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

    @Override
    protected String getBackground() {
        return "wtlib/gui/crafting.png";
    }

    public static void drawEntity(int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
        float f = (float) Math.atan((mouseX / 40.0F));
        float g = (float) Math.atan((mouseY / 40.0F));
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float) x, (float) y, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(0.0D, 0.0D, 1000.0D);
        matrixStack.scale((float) size, (float) size, (float) size);
        Quaternion quaternion = Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
        Quaternion quaternion2 = Vector3f.POSITIVE_X.getDegreesQuaternion(g * 20.0F);
        quaternion.hamiltonProduct(quaternion2);
        matrixStack.multiply(quaternion);
        float h = entity.bodyYaw;
        float i = entity.yaw;
        float j = entity.pitch;
        float k = entity.prevHeadYaw;
        float l = entity.headYaw;
        entity.bodyYaw = 180.0F + f * 20.0F;
        entity.yaw = 180.0F + f * 40.0F;
        entity.pitch = -g * 20.0F;
        entity.headYaw = entity.yaw;
        entity.prevHeadYaw = entity.yaw;
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        quaternion2.conjugate();
        entityRenderDispatcher.setRotation(quaternion2);
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStack, immediate, 15728880));
        immediate.draw();
        entityRenderDispatcher.setRenderShadows(true);
        entity.bodyYaw = h;
        entity.yaw = i;
        entity.pitch = j;
        entity.prevHeadYaw = k;
        entity.headYaw = l;
        RenderSystem.popMatrix();
    }

    @Override
    public List<Rectangle> getExclusionZones() {
        List<Rectangle> zones = super.getExclusionZones();
        zones.add(new Rectangle(x + 195, y, 24, backgroundHeight - 110));
        return zones;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.mouseX = (float) mouseX;
        this.mouseY = (float) mouseY;
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void tick() {
        super.tick();
        if(!Config.allowTrinket()) return;
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
                    if(group.vanillaSlot != -1)
                        TrinketsClient.activeSlots.add(getScreenHandler().getSlot(group.vanillaSlot));
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
                    && (TrinketsClient.slotGroup == null || !ts.group.equals(TrinketsClient.slotGroup.getName()))) && !ts.keepVisible) {
                ((SlotMixin) ts).setXPosition(Integer.MIN_VALUE);
            }
        }
        for(AppEngTrinketSlot ts : trinketSlots) {
            int groupX = getGroupX(TrinketSlots.getSlotFromName(ts.group, ts.slot).getSlotGroup());
            if(ts.keepVisible && groupX < 0) ((SlotMixin) ts).setXPosition(groupX + 1);
        }
    }

    @Override
    protected boolean isClickOutsideBounds(double x, double y, int i, int j, int k) {
        if(TrinketsClient.slotGroup != null && inBounds(TrinketsClient.slotGroup, (float) x - this.x, (float) y - this.y, true))
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
                if(j < 8) return 76;
                return -15 - ((j - 8) / 4) * 18;
            } else if(TrinketSlots.slotGroups.get(i).slots.size() == 0) j--;
        }
        return 0;
    }

    public int getGroupY(TrinketSlots.SlotGroup group) {
        if(group.vanillaSlot == 5) return -160 + backgroundHeight;
        if(group.vanillaSlot == 6) return -142 + backgroundHeight;
        if(group.vanillaSlot == 7) return -124 + backgroundHeight;
        if(group.vanillaSlot == 8) return -106 + backgroundHeight;
        if(group.vanillaSlot == 45) return -106 + backgroundHeight;
        if(group.getName().equals(SlotGroups.HAND)) return -106 + backgroundHeight;
        int j = 0;
        if(TrinketSlots.slotGroups.get(5).slots.size() == 0) j = -1;
        for(int i = 6; i < TrinketSlots.slotGroups.size(); i++) {
            if(TrinketSlots.slotGroups.get(i) == group) {
                j += i;
                if(j == 5) return 43;
                if(j == 6) return 25;
                if(j == 7) return 7;
                return 7 + ((j - 8) % 4) * 18;
            } else if(TrinketSlots.slotGroups.get(i).slots.size() == 0) j--;
        }
        return 0;
    }

    private static final Identifier BLANK_BACK = new Identifier("trinkets", "textures/gui/blank_back.png");

    public void renderSlotBack(MatrixStack matrices, Slot ts, TrinketSlots.Slot s, int x, int y) {
        assert client != null;
        RenderSystem.disableLighting();
        if(ts.getStack().isEmpty()) client.getTextureManager().bindTexture(s.texture);
        else client.getTextureManager().bindTexture(BLANK_BACK);
        DrawableHelper.drawTexture(matrices, x + ts.x, y + ts.y, 0, 0, 0, 16, 16, 16, 16);
    }

    public void renderSlot(MatrixStack matrices, Slot ts, TrinketSlots.Slot s, int x, int y) {
        assert client != null;
        matrices.push();
        RenderSystem.disableLighting();
        RenderSystem.disableDepthTest();
        if(ts.getStack().isEmpty()) client.getTextureManager().bindTexture(s.texture);
        else client.getTextureManager().bindTexture(BLANK_BACK);
        DrawableHelper.drawTexture(matrices, ts.x, ts.y, 0, 0, 0, 16, 16, 16, 16);
        ((ScreenMixin) this).invokeDrawSlot(matrices, ts);
        if(isPointOverSlot(ts, x, y) && ts.doDrawHoveringEffect()) {
            focusedSlot = ts;
            RenderSystem.disableDepthTest();
            RenderSystem.colorMask(true, true, true, false);
            fillGradient(matrices, ts.x, ts.y, ts.x + 16, ts.y + 16, -2130706433, -2130706433);
            RenderSystem.colorMask(true, true, true, true);
        }
        matrices.pop();
    }

    private boolean isPointOverSlot(Slot slot, double a, double b) {
        if(TrinketsClient.slotGroup == null && slot instanceof AppEngTrinketSlot) return false;
        if(TrinketsClient.activeSlots != null && TrinketsClient.activeSlots.contains(slot))
            return isPointWithinBounds(slot.x, slot.y, 16, 16, a, b);
        return false;
    }
}