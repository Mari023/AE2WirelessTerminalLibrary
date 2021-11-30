package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingPlan;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.menu.AEBaseMenu;
import appeng.menu.MenuOpener;
import appeng.menu.me.crafting.CraftConfirmMenu;
import de.mari_023.fabric.ae2wtlib.wct.WCTMenu;
import de.mari_023.fabric.ae2wtlib.wct.WCTMenuHost;
import de.mari_023.fabric.ae2wtlib.wet.WETMenu;
import de.mari_023.fabric.ae2wtlib.wet.WETMenuHost;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CraftConfirmMenu.class, remap = false)
public abstract class CraftConfirmContainerMixin extends AEBaseMenu {

    @Shadow
    private ICraftingCPU selectedCpu;
    @Shadow
    private ICraftingPlan result;

    public CraftConfirmContainerMixin() {
        super(null, 0, null, null);
    }

    @Shadow
    protected abstract IGrid getGrid();

    @Shadow
    protected abstract IActionSource getActionSrc();

    @Shadow
    public abstract void setAutoStart(boolean autoStart);

    @Inject(method = "startJob", at = @At(value = "HEAD"))
    public void serverPacketData(CallbackInfo ci) {
        ScreenHandlerType<?> originalGui = null;
        IActionHost ah = getActionHost();
        if(ah instanceof WCTMenuHost) originalGui = WCTMenu.TYPE;
        else if(ah instanceof WETMenuHost) originalGui = WETMenu.TYPE;

        if(result == null || result.simulation()) return;

        ICraftingLink g = getGrid().getCraftingService().submitJob(result, null, selectedCpu, true, getActionSrc());
        setAutoStart(false);
        if(g != null && originalGui != null && getLocator() != null)
            MenuOpener.open(originalGui, getPlayerInventory().player, getLocator());
    }
}
