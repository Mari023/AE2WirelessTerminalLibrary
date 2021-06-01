package appeng.container.implementations;

import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.storage.ITerminalHost;
import appeng.container.ContainerLocator;
import appeng.container.guisync.GuiSync;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import de.mari_023.fabric.ae2wtlib.util.ContainerHelper;

public class WirelessCraftingStatusContainer extends CraftingCPUContainer implements CraftingCPUCyclingContainer {
    public static ScreenHandlerType<WirelessCraftingStatusContainer> TYPE;

    public static final ContainerHelper<WirelessCraftingStatusContainer, ITerminalHost> helper = new ContainerHelper<>(WirelessCraftingStatusContainer::new, ITerminalHost.class);

    public static WirelessCraftingStatusContainer fromNetwork(int windowId, PlayerInventory inv, PacketByteBuf buf) {
        return helper.fromNetwork(windowId, inv, buf);
    }

    public static boolean open(PlayerEntity player, ContainerLocator locator) {
        return helper.open(player, locator);
    }

    public WirelessCraftingStatusContainer(int id, PlayerInventory ip, ITerminalHost terminalHost) {
        super(TYPE, id, ip, terminalHost);
    }

    private final CraftingCPUCycler cpuCycler = new CraftingCPUCycler(this::cpuMatches, this::onCPUSelectionChanged);

    @GuiSync(6)
    public boolean noCPU = true;

    @GuiSync(7)
    public Text cpuName;

    @Override
    public void sendContentUpdates() {
        IGrid network = getNetwork();
        if(isServer() && network != null) cpuCycler.detectAndSendChanges(network);
        super.sendContentUpdates();
    }

    private boolean cpuMatches(final ICraftingCPU c) {
        return c.isBusy();
    }

    private void onCPUSelectionChanged(CraftingCPURecord cpuRecord, boolean cpusAvailable) {
        noCPU = !cpusAvailable;
        if(cpuRecord == null) {
            cpuName = null;
            setCPU(null);
        } else {
            cpuName = cpuRecord.getName();
            setCPU(cpuRecord.getCpu());
        }
    }

    @Override
    public void cycleSelectedCPU(boolean forward) {
        cpuCycler.cycleCpu(forward);
    }
}