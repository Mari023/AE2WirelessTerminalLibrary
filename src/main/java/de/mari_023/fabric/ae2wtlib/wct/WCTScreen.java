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
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.MagnetMode;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.MagnetSettings;
import de.mari_023.fabric.ae2wtlib.wut.CycleTerminalButton;
import de.mari_023.fabric.ae2wtlib.wut.IUniversalTerminalCapable;
import me.shedaniel.math.Rectangle;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
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
import java.util.List;

public class WCTScreen extends MEMonitorableScreen<WCTContainer> implements IUniversalTerminalCapable {

    private int rows = 0;
    private AETextField searchField;
    private final int reservedSpace;
    //private TabButton craftingStatusBtn;
    IconButton magnetCardToggleButton;
    private final WCTContainer container;

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

        magnetCardToggleButton = addButton(new IconButton(x + 92 + 60, y + backgroundHeight - 114, btn -> setMagnetMode()) {
            @Override
            protected int getIconIndex() {
                return 6;
            }
        });
        magnetCardToggleButton.setHalfSize(true);
        resetMagnetSettings();
        container.setScreen(this);

        if(container.isWUT()) addButton(new CycleTerminalButton(x - 18, y + 88, btn -> cycleTerminal()));

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
    }

    @Override
    public void drawBG(MatrixStack matrices, final int offsetX, final int offsetY, final int mouseX, final int mouseY, float partialTicks) {

        bindTexture(getBackground());
        final int x_width = 197;
        drawTexture(matrices, offsetX, offsetY, 0, 0, x_width, 18);

        for(int x = 0; x < this.rows; x++) {
            drawTexture(matrices, offsetX, offsetY + 18 + x * 18, 0, 18, x_width, 18);
        }

        drawTexture(matrices, offsetX, offsetY + 16 + rows * 18, 0, 106 - 18 - 18, x_width, 99 + reservedSpace);

        searchField.render(matrices, mouseX, mouseY, partialTicks);

        if(client != null && client.player != null)
            drawEntity(offsetX + 52, offsetY + 94 + rows * 18, 30, (float) (offsetX + 52) - mouseX, (float) offsetY + 55 + rows * 18 - mouseY, client.player);
    }

    @Override
    public void drawFG(MatrixStack matrices, final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        super.drawFG(matrices, offsetX, offsetY, mouseX, mouseY);
        textRenderer.draw(matrices, GuiText.CraftingTerminal.text(), 8, backgroundHeight - 96 + 1 - reservedSpace, 4210752);
    }

    private void clear() {
        Slot s = null;
        for(final Slot j : handler.slots) {
            if(j instanceof CraftingMatrixSlot) {
                s = j;
            }
        }

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
}