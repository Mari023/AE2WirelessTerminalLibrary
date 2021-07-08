package appeng.container.me.crafting;

import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.crafting.ICraftingJob;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.data.IAEItemStack;
import appeng.container.AEBaseContainer;
import appeng.container.ContainerLocator;
import appeng.container.guisync.GuiSync;
import appeng.core.AELog;
import appeng.core.sync.packets.CraftConfirmPlanPacket;
import appeng.me.helpers.PlayerSource;
import de.mari_023.fabric.ae2wtlib.terminal.WTGuiObject;
import de.mari_023.fabric.ae2wtlib.util.ContainerHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.concurrent.Future;

public class WirelessCraftConfirmContainer extends AEBaseContainer implements CraftingCPUCyclingContainer {
    private static final String ACTION_BACK = "back";

    public static ScreenHandlerType<WirelessCraftConfirmContainer> TYPE;

    private static final ContainerHelper<WirelessCraftConfirmContainer, ITerminalHost> helper = new ContainerHelper<>(
            WirelessCraftConfirmContainer::new, ITerminalHost.class, SecurityPermissions.CRAFT);

    public static WirelessCraftConfirmContainer fromNetwork(int windowId, PlayerInventory inv, PacketByteBuf buf) {
        return helper.fromNetwork(windowId, inv, buf);
    }

    public static boolean open(PlayerEntity player, ContainerLocator locator) {
        return helper.open(player, locator);
    }

    private final CraftingCPUCycler cpuCycler;

    private ICraftingCPU selectedCpu;

    private IAEItemStack itemToCreate;
    private Future<ICraftingJob> job;
    private ICraftingJob result;

    @GuiSync(3)
    public boolean autoStart = false;

    // Indicates whether any CPUs are available
    @GuiSync(6)
    public boolean noCPU = true;

    // Properties of the currently selected crafting CPU, this can be null
    // if no CPUs are available, or if an automatic one is selected
    @GuiSync(1)
    public long cpuBytesAvail;
    @GuiSync(2)
    public int cpuCoProcessors;
    @GuiSync(7)
    public Text cpuName;

    private CraftingPlanSummary plan;

    public WirelessCraftConfirmContainer(int id, PlayerInventory ip, ITerminalHost te) {
        super(TYPE, id, ip, te);
        cpuCycler = new CraftingCPUCycler(this::cpuMatches, this::onCPUSelectionChanged);
        // A player can select no crafting CPU to use a suitable one automatically
        cpuCycler.setAllowNoSelection(true);

        registerClientAction(ACTION_BACK, this::goBack);
    }

    @Override
    public void cycleSelectedCPU(final boolean next) {
        cpuCycler.cycleCpu(next);
    }

    @Override
    public void sendContentUpdates() {
        if(isClient()) return;

        cpuCycler.detectAndSendChanges(getGrid());

        super.sendContentUpdates();

        if(job != null && job.isDone()) {
            try {
                result = job.get();

                if(!result.isSimulation() && isAutoStart()) {
                    startJob();
                    return;
                }

                plan = CraftingPlanSummary.fromJob(getGrid(), getActionSrc(), result);

                sendPacketToClient(new CraftConfirmPlanPacket(plan));
            } catch(final Throwable e) {
                getPlayerInventory().player.sendMessage(new LiteralText("Error: " + e), false);
                AELog.debug(e);
                setValidContainer(false);
                result = null;
            }

            setJob(null);
        }
        verifyPermissions(SecurityPermissions.CRAFT, false);
    }

    private IGrid getGrid() {
        final IActionHost h = (IActionHost) getTarget();
        return h.getActionableNode().getGrid();
    }

    private boolean cpuMatches(final ICraftingCPU c) {
        if(plan == null) return true;
        return c.getAvailableStorage() >= plan.getUsedBytes() && !c.isBusy();
    }

    public void startJob() {
        final IActionHost ah = getActionHost();
        if(!(ah instanceof WTGuiObject)) return;
        if(result == null && result.isSimulation()) return;

        setAutoStart(false);
        if(((ICraftingGrid) getGrid().getCache(ICraftingGrid.class)).submitJob(result, null, selectedCpu, true, getActionSrc()) != null && getLocator() != null)
            ((WTGuiObject) ah).open(getPlayerInventory().player, getLocator());
    }

    private IActionSource getActionSrc() {
        return new PlayerSource(getPlayerInventory().player, (IActionHost) getTarget());
    }

    @Override
    public void removeListener(final ScreenHandlerListener c) {
        super.removeListener(c);
        if(job == null) return;
        job.cancel(true);
        setJob(null);
    }

    @Override
    public void close(final PlayerEntity par1PlayerEntity) {
        super.close(par1PlayerEntity);
        if(job == null) return;
        job.cancel(true);
        setJob(null);
    }

    private void onCPUSelectionChanged(CraftingCPURecord cpuRecord, boolean cpusAvailable) {
        noCPU = !cpusAvailable;

        if(cpuRecord == null) {
            cpuBytesAvail = 0;
            cpuCoProcessors = 0;
            cpuName = null;
            selectedCpu = null;
        } else {
            cpuBytesAvail = cpuRecord.getSize();
            cpuCoProcessors = cpuRecord.getProcessors();
            cpuName = cpuRecord.getName();
            selectedCpu = cpuRecord.getCpu();
        }
    }

    public World getWorld() {
        return getPlayerInventory().player.world;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(final boolean autoStart) {
        this.autoStart = autoStart;
    }

    public long getCpuAvailableBytes() {
        return cpuBytesAvail;
    }

    public int getCpuCoProcessors() {
        return cpuCoProcessors;
    }

    public Text getName() {
        return cpuName;
    }

    public boolean hasNoCPU() {
        return noCPU;
    }

    public void setItemToCreate(IAEItemStack itemToCreate) {
        this.itemToCreate = itemToCreate;
    }

    public void setJob(final Future<ICraftingJob> job) {
        this.job = job;
    }

    public CraftingPlanSummary getPlan() {
        return plan;
    }

    public void setPlan(CraftingPlanSummary plan) {
        this.plan = plan;
    }

    public void goBack() {
        PlayerEntity player = getPlayerInventory().player;
        if(player instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            if(itemToCreate != null)
                CraftAmountContainer.open(serverPlayer, getLocator(), itemToCreate, (int) itemToCreate.getStackSize());
        } else sendClientAction(ACTION_BACK);
    }
}