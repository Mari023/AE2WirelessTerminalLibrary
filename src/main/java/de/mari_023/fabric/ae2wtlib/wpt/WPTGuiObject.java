package de.mari_023.fabric.ae2wtlib.wpt;

import alexiil.mc.lib.attributes.item.FixedItemInv;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.implementations.guiobjects.IPortableCell;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.core.Api;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.inv.IAEAppEngInventory;
import appeng.util.inv.InvOperation;
import de.mari_023.fabric.ae2wtlib.FixedViewCellInventory;
import de.mari_023.fabric.ae2wtlib.terminal.WTGUIObject;
import de.mari_023.fabric.ae2wtlib.terminal.ae2wtlibInternalInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class WPTGuiObject extends WTGUIObject implements IPortableCell, IAEAppEngInventory/*, IViewCellStorage*/ {

    private final FixedViewCellInventory fixedViewCellInventory = new FixedViewCellInventory();
    private boolean craftingMode = true;
    private boolean substitute = false;
    private final AppEngInternalInventory crafting;
    private final AppEngInternalInventory output;
    private final AppEngInternalInventory pattern;

    public WPTGuiObject(final IWirelessTermHandler wh, final ItemStack is, final PlayerEntity ep, int inventorySlot) {
        super(wh, is, ep, inventorySlot);
        crafting = new ae2wtlibInternalInventory(this, 9, "crafting", is);
        output = new ae2wtlibInternalInventory(this, 3, "output", is);
        pattern = new ae2wtlibInternalInventory(this, 2, "pattern", is);
    }

    @Override
    public <T extends IAEStack<T>> IMEMonitor<T> getInventory(IStorageChannel<T> channel) {
        return getIStorageGrid().getInventory(channel);
    }

    @Override
    public void addListener(final IMEMonitorHandlerReceiver<IAEItemStack> l, final Object verificationToken) {
        if(getItemStorage() != null) {
            getItemStorage().addListener(l, verificationToken);
        }
    }

    @Override
    public void removeListener(final IMEMonitorHandlerReceiver<IAEItemStack> l) {
        if(getItemStorage() != null) {
            getItemStorage().removeListener(l);
        }
    }

    @Override
    public IItemList<IAEItemStack> getAvailableItems(final IItemList<IAEItemStack> out) {
        if(getItemStorage() != null) {
            return getItemStorage().getAvailableItems(out);
        }
        return out;
    }

    @Override
    public IItemList<IAEItemStack> getStorageList() {
        if(getItemStorage() != null) {
            return getItemStorage().getStorageList();
        }
        return null;
    }

    @Override
    public AccessRestriction getAccess() {
        if(getItemStorage() != null) {
            return getItemStorage().getAccess();
        }
        return AccessRestriction.NO_ACCESS;
    }

    @Override
    public boolean isPrioritized(final IAEItemStack input) {
        if(getItemStorage() != null) {
            return getItemStorage().isPrioritized(input);
        }
        return false;
    }

    @Override
    public boolean canAccept(final IAEItemStack input) {
        if(getItemStorage() != null) {
            return getItemStorage().canAccept(input);
        }
        return false;
    }

    @Override
    public int getPriority() {
        if(getItemStorage() != null) {
            return getItemStorage().getPriority();
        }
        return 0;
    }

    @Override
    public int getSlot() {
        if(getItemStorage() != null) {
            return getItemStorage().getSlot();
        }
        return 0;
    }

    @Override
    public boolean validForPass(final int i) {
        return getItemStorage().validForPass(i);
    }

    @Override
    public IAEItemStack injectItems(final IAEItemStack input, final Actionable type, final IActionSource src) {
        if(getItemStorage() != null) {
            return getItemStorage().injectItems(input, type, src);
        }
        return input;
    }

    @Override
    public IAEItemStack extractItems(final IAEItemStack request, final Actionable mode, final IActionSource src) {
        if(getItemStorage() != null) {
            return getItemStorage().extractItems(request, mode, src);
        }
        return null;
    }

    @Override
    public IStorageChannel getChannel() {
        if(getItemStorage() != null) {
            return getItemStorage().getChannel();
        }
        return Api.instance().storage().getStorageChannel(IItemStorageChannel.class);
    }

    public boolean isCraftingRecipe() {
        return craftingMode;
    }

    public FixedItemInv getInventoryByName(final String name) {
        if(name.equals("crafting")) {
            return crafting;
        }

        if(name.equals("output")) {
            return output;
        }

        if(name.equals("pattern")) {
            return pattern;
        }
        return null;
    }

    @Override
    public void saveChanges() {}

    @Override
    public void onChangeInventory(FixedItemInv inv, int slot, InvOperation mc, ItemStack removedStack, ItemStack newStack) {
        if(inv == pattern && slot == 1) {
            final ItemStack is = pattern.getInvStack(1);
            final ICraftingPatternDetails details = Api.instance().crafting().decodePattern(is, getPlayer().world, false);
            if(details != null) {
                setCraftingRecipe(details.isCraftable());
                setSubstitution(details.canSubstitute());

                for(int x = 0; x < crafting.getSlotCount() && x < details.getSparseInputs().length; x++) {
                    final IAEItemStack item = details.getSparseInputs()[x];
                    crafting.forceSetInvStack(x, item == null ? ItemStack.EMPTY : item.createItemStack());
                }

                for(int x = 0; x < output.getSlotCount() && x < details.getSparseOutputs().length; x++) {
                    final IAEItemStack item = details.getSparseOutputs()[x];
                    output.forceSetInvStack(x, item == null ? ItemStack.EMPTY : item.createItemStack());
                }
            }
        } else if(inv == crafting) {
            fixCraftingRecipes();
        }
    }

    public void setCraftingRecipe(final boolean craftingMode) {
        this.craftingMode = craftingMode;
        fixCraftingRecipes();
    }

    public boolean isSubstitution() {
        return this.substitute;
    }

    public void setSubstitution(final boolean canSubstitute) {
        this.substitute = canSubstitute;
    }

    private void fixCraftingRecipes() {
        if(craftingMode) {
            for(int x = 0; x < crafting.getSlotCount(); x++) {
                final ItemStack is = crafting.getInvStack(x);
                if(!is.isEmpty()) {
                    is.setCount(1);
                }
            }
        }
    }

    /*@Override
    public FixedViewCellInventory getViewCellStorage() { //FIXME viemcells
        return fixedViewCellInventory;
    }*/
}