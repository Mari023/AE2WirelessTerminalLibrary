package de.mari_023.fabric.ae2wtlib.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin extends Screen {

    private HandledScreenMixin() {
        super(null);
    }

    //FIXME currently disabled, need to fix when enabling trinkets displaying. breaks trinkets in vanilla inventories
    /*@Unique
    private static final Identifier MORE_SLOTS = new Identifier("trinkets", "textures/gui/more_slots.png");
    @Unique
    private static final Identifier BLANK_BACK = new Identifier("trinkets", "textures/gui/blank_back.png");

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/item/ItemRenderer;zOffset:F", opcode = Opcodes.PUTFIELD, ordinal = 0, shift = At.Shift.AFTER), method = "drawSlot")
    private void changeZ(MatrixStack matrices, Slot slot, CallbackInfo info) {
        // Items are drawn at z + 150 (normal items are drawn at 250)
        // Item tooltips (count, item bar) are drawn at z + 200 (normal items are drawn at 300)
        // Inventory tooltip is drawn at 400
        if(slot instanceof AppEngTrinketSlot ts) {
            assert client != null;
            Identifier id = ts.getBackgroundIdentifier();

            if(slot.getStack().isEmpty() && id != null) {
                RenderSystem.setShaderTexture(0, id);
            } else {
                RenderSystem.setShaderTexture(0, BLANK_BACK);
            }

            RenderSystem.enableDepthTest();

            if(ts.isTrinketFocused()) {
                // Thus, I need to draw trinket slot backs over normal items at z 300 (310 was chosen)
                drawTexture(matrices, slot.x, slot.y, 310, 0, 0, 16, 16, 16, 16);
                // I also need to draw items in trinket slots *above* 310 but *below* 400, (320 for items and 370 for tooltips was chosen)
                itemRenderer.zOffset = 170F;
            } else {
                drawTexture(matrices, slot.x, slot.y, 0, 0, 0, 16, 16, 16, 16);
                RenderSystem.setShaderTexture(0, MORE_SLOTS);
                drawTexture(matrices, slot.x - 1, slot.y - 1, 0, 4, 4, 18, 18, 256, 256);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "isPointOverSlot", cancellable = true)
    private void isPointOverSlot(Slot slot, double pointX, double pointY, CallbackInfoReturnable<Boolean> info) {
        if(TrinketsClient.activeGroup != null) {
            if(slot instanceof AppEngTrinketSlot ts) {
                if(!ts.isTrinketFocused()) {
                    info.setReturnValue(false);
                }
            } else {
                if(slot.id != TrinketsClient.activeGroup.getSlotId()) {
                    info.setReturnValue(false);
                }
            }
        }
    }*/
}