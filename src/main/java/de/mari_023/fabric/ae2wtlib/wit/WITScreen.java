package de.mari_023.fabric.ae2wtlib.wit;

import alexiil.mc.lib.attributes.Simulation;
import appeng.api.config.Settings;
import appeng.api.config.TerminalStyle;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.client.ActionKey;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.widgets.AETextField;
import appeng.client.gui.widgets.Scrollbar;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.client.me.ClientDCInternalInv;
import appeng.client.me.SlotDisconnected;
import appeng.container.slot.AppEngSlot;
import appeng.core.AEConfig;
import appeng.core.Api;
import appeng.core.AppEng;
import appeng.core.localization.GuiText;
import appeng.util.Platform;
import com.google.common.collect.HashMultimap;
import de.mari_023.fabric.ae2wtlib.mixin.SlotMixin;
import de.mari_023.fabric.ae2wtlib.wut.CycleTerminalButton;
import de.mari_023.fabric.ae2wtlib.wut.IUniversalTerminalCapable;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Rect2i;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.*;

public class WITScreen extends AEBaseScreen<WITContainer> implements IUniversalTerminalCapable {
    private static final Rect2i HEADER_BBOX = new Rect2i(0, 0, 195, 17);
    private static final Rect2i ROW_TEXT_TOP_BBOX = new Rect2i(0, 17, 195, 18);
    private static final Rect2i ROW_TEXT_MIDDLE_BBOX = new Rect2i(0, 53, 195, 18);
    private static final Rect2i ROW_TEXT_BOTTOM_BBOX = new Rect2i(0, 89, 195, 18);
    private static final Rect2i ROW_INVENTORY_TOP_BBOX = new Rect2i(0, 35, 195, 18);
    private static final Rect2i ROW_INVENTORY_MIDDLE_BBOX = new Rect2i(0, 71, 195, 18);
    private static final Rect2i ROW_INVENTORY_BOTTOM_BBOX = new Rect2i(0, 107, 195, 18);
    private static final Rect2i FOOTER_BBOX = new Rect2i(0, 125, 195, 97);
    private final HashMap<Long, ClientDCInternalInv> byId = new HashMap<>();
    private final HashMultimap<String, ClientDCInternalInv> byName = HashMultimap.create();
    private final ArrayList<String> names = new ArrayList<>();
    private final ArrayList<Object> lines = new ArrayList<>();
    private final Map<String, Set<Object>> cachedSearches = new WeakHashMap<>();
    private boolean refreshList = false;
    private AETextField searchField;
    private int numLines = 0;

    public WITScreen(WITContainer container, PlayerInventory playerInventory, Text title) {
        super(container, playerInventory, title);
        Scrollbar scrollbar = new Scrollbar();
        setScrollBar(scrollbar);
        backgroundWidth = 195;
    }

    public void init() {
        TerminalStyle terminalStyle = AEConfig.instance().getTerminalStyle();
        int maxLines = terminalStyle == TerminalStyle.SMALL ? 6 : 2147483647;
        numLines = (height - 17 - 97) / 18;
        numLines = MathHelper.clamp(numLines, 3, maxLines);
        backgroundHeight = 114 + numLines * 18;
        super.init();
        searchField = new AETextField(textRenderer, x + 104, y + 4, 65, 12);
        searchField.setDrawsBackground(false);
        searchField.setMaxLength(25);
        searchField.setEditableColor(16777215);
        searchField.setVisible(true);
        searchField.setChangedListener((str) -> refreshList());
        addChild(searchField);
        changeFocus(true);
        int offset = y + 8;
        addButton(new SettingToggleButton<>(x + -18, offset, Settings.TERMINAL_STYLE, terminalStyle, this::toggleTerminalStyle));

        for(Slot s : (handler).slots)
            if(s instanceof AppEngSlot) ((SlotMixin) s).setY(((AppEngSlot) s).getY() + backgroundHeight - 83);

        resetScrollbar();
        if(handler.isWUT()) addButton(new CycleTerminalButton(x - 18, offset + 20, btn -> cycleTerminal()));
    }

    public void drawFG(MatrixStack matrixStack, int offsetX, int offsetY, int mouseX, int mouseY) {
        textRenderer.draw(matrixStack, getGuiDisplayName(GuiText.InterfaceTerminal.text()).getString(), 8.0F, 6.0F, 4210752);
        (handler).slots.removeIf((slot) -> slot instanceof SlotDisconnected);
        int scrollLevel = getScrollBar().getCurrentScroll();

        int i;
        for(i = 0; i < numLines; ++i) {
            if(scrollLevel + i < lines.size()) {
                Object lineObj = lines.get(scrollLevel + i);
                int rows;
                if(lineObj instanceof ClientDCInternalInv) {
                    ClientDCInternalInv inv = (ClientDCInternalInv) lineObj;

                    for(rows = 0; rows < inv.getInventory().getSlotCount(); ++rows)
                        (handler).slots.add(new SlotDisconnected(inv, rows, rows * 18 + 8, (i + 1) * 18));
                } else if(lineObj instanceof String) {
                    String name = (String) lineObj;
                    rows = byName.get(name).size();
                    if(rows > 1) name = name + " (" + rows + ')';

                    name = textRenderer.trimToWidth(name, 155, true);
                    textRenderer.draw(matrixStack, name, 10.0F, (float) (23 + i * 18), 4210752);
                }
            }
        }
        textRenderer.draw(matrixStack, GuiText.inventory.text().getString(), 8.0F, (float) (20 + i * 18), 4210752);
    }

    public boolean mouseClicked(double xCoord, double yCoord, int btn) {
        if(searchField.mouseClicked(xCoord, yCoord, btn)) return true;
        else if(btn == 1 && searchField.isMouseOver(xCoord, yCoord)) {
            searchField.setText("");
            return true;
        } else return super.mouseClicked(xCoord, yCoord, btn);
    }

    public void drawBG(MatrixStack matrixStack, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
        bindTexture("wtlib/gui/interface.png");
        blit(matrixStack, offsetX, offsetY, HEADER_BBOX);
        int scrollLevel = getScrollBar().getCurrentScroll();
        int currentY = offsetY + 17;
        blit(matrixStack, offsetX, currentY + numLines * 18, FOOTER_BBOX);

        for(int i = 0; i < numLines; ++i) {
            boolean firstLine = i == 0;
            boolean lastLine = i == numLines - 1;
            boolean isInvLine = false;
            if(scrollLevel + i < lines.size()) {
                Object lineObj = lines.get(scrollLevel + i);
                isInvLine = lineObj instanceof ClientDCInternalInv;
            }

            Rect2i bbox = selectRowBackgroundBox(isInvLine, firstLine, lastLine);
            blit(matrixStack, offsetX, currentY, bbox);
            currentY += 18;
        }
        if(searchField != null) searchField.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private Rect2i selectRowBackgroundBox(boolean isInvLine, boolean firstLine, boolean lastLine) {
        if(isInvLine)
            if(firstLine) return ROW_INVENTORY_TOP_BBOX;
            else return lastLine ? ROW_INVENTORY_BOTTOM_BBOX : ROW_INVENTORY_MIDDLE_BBOX;
        else if(firstLine) return ROW_TEXT_TOP_BBOX;
        else return lastLine ? ROW_TEXT_BOTTOM_BBOX : ROW_TEXT_MIDDLE_BBOX;
    }

    public boolean charTyped(char character, int key) {
        return character == ' ' && searchField.getText().isEmpty() || super.charTyped(character, key);
    }

    public boolean keyPressed(int keyCode, int scanCode, int p_keyPressed_3_) {
        InputUtil.Key input = InputUtil.fromKeyCode(keyCode, scanCode);
        if(keyCode != 256) {
            if(AppEng.instance().isActionKey(ActionKey.TOGGLE_FOCUS, input)) {
                searchField.setTextFieldFocused(!searchField.isFocused());
                return true;
            }

            if(searchField.isFocused()) {
                if(keyCode == 257) {
                    searchField.setTextFieldFocused(false);
                    return true;
                }
                searchField.keyPressed(keyCode, scanCode, p_keyPressed_3_);
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, p_keyPressed_3_);
    }

    public void postUpdate(CompoundTag in) {
        if(in.getBoolean("clear")) {
            byId.clear();
            refreshList = true;
        }

        Iterator<String> var2 = in.getKeys().iterator();

        while(true) {
            String key;
            do {
                if(!var2.hasNext()) {
                    if(refreshList) {
                        refreshList = false;
                        cachedSearches.clear();
                        refreshList();
                    }
                    return;
                }

                key = var2.next();
            } while(!key.startsWith("="));

            try {
                long id = Long.parseLong(key.substring(1), 36);
                CompoundTag invData = in.getCompound(key);
                Text un = Text.Serializer.fromJson(invData.getString("un"));
                ClientDCInternalInv current = getById(id, invData.getLong("sortBy"), un);

                for(int x = 0; x < current.getInventory().getSlotCount(); ++x) {
                    String which = Integer.toString(x);
                    if(invData.contains(which))
                        current.getInventory().setInvStack(x, ItemStack.fromTag(invData.getCompound(which)), Simulation.ACTION);
                }
            } catch(NumberFormatException ignored) {}
        }
    }

    private void refreshList() {
        byName.clear();
        String searchFilterLowerCase = searchField.getText().toLowerCase();
        Set<Object> cachedSearch = getCacheForSearchTerm(searchFilterLowerCase);
        boolean rebuild = cachedSearch.isEmpty();
        Iterator var4 = byId.values().iterator();

        while(true) {
            ClientDCInternalInv entry;
            do {
                if(!var4.hasNext()) {
                    names.clear();
                    names.addAll(byName.keySet());
                    Collections.sort(names);
                    lines.clear();
                    lines.ensureCapacity(getMaxRows());
                    var4 = names.iterator();

                    while(var4.hasNext()) {
                        String n = (String) var4.next();
                        lines.add(n);
                        List<ClientDCInternalInv> clientInventories = new ArrayList<>(byName.get(n));
                        Collections.sort(clientInventories);
                        lines.addAll(clientInventories);
                    }
                    resetScrollbar();
                    return;
                }

                entry = (ClientDCInternalInv) var4.next();
            } while(!rebuild && !cachedSearch.contains(entry));

            boolean found = searchFilterLowerCase.isEmpty();
            if(!found) {

                for(ItemStack itemStack : entry.getInventory()) {
                    found = itemStackMatchesSearchTerm(itemStack, searchFilterLowerCase);
                    if(found) break;
                }
            }

            if(!found && !entry.getSearchName().contains(searchFilterLowerCase)) cachedSearch.remove(entry);
            else {
                byName.put(entry.getFormattedName(), entry);
                cachedSearch.add(entry);
            }
        }
    }

    private void resetScrollbar() {
        Scrollbar bar = getScrollBar();
        bar.setLeft(175).setTop(18).setHeight(numLines * 18 - 2);
        bar.setRange(0, lines.size() - numLines, 2);
    }

    private boolean itemStackMatchesSearchTerm(ItemStack itemStack, String searchTerm) {
        if(!itemStack.isEmpty()) {
            CompoundTag encodedValue = itemStack.getTag();
            if(encodedValue != null) {
                ListTag outTag = encodedValue.getList("out", 10);

                for(int i = 0; i < outTag.size(); ++i) {
                    ItemStack parsedItemStack = ItemStack.fromTag(outTag.getCompound(i));
                    if(!parsedItemStack.isEmpty()) {
                        String displayName = Platform.getItemDisplayName(Api.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(parsedItemStack)).getString().toLowerCase();
                        if(displayName.contains(searchTerm)) return true;
                    }
                }
            }
        }
        return false;
    }

    private Set<Object> getCacheForSearchTerm(String searchTerm) {
        if(!cachedSearches.containsKey(searchTerm)) cachedSearches.put(searchTerm, new HashSet<>());

        Set<Object> cache = cachedSearches.get(searchTerm);
        if(cache.isEmpty() && searchTerm.length() > 1)
            cache.addAll(getCacheForSearchTerm(searchTerm.substring(0, searchTerm.length() - 1)));
        return cache;
    }

    private void reinitialize() {
        children.removeAll(buttons);
        buttons.clear();
        init();
    }

    private void toggleTerminalStyle(SettingToggleButton<TerminalStyle> btn, boolean backwards) {
        TerminalStyle next = btn.getNextValue(backwards);
        AEConfig.instance().setTerminalStyle(next);
        btn.set(next);
        reinitialize();
    }

    private int getMaxRows() {
        return names.size() + byId.size();
    }

    private ClientDCInternalInv getById(long id, long sortBy, Text name) {
        ClientDCInternalInv o = byId.get(id);
        if(o == null) {
            byId.put(id, o = new ClientDCInternalInv(9, id, sortBy, name));
            refreshList = true;
        }
        return o;
    }

    private void blit(MatrixStack matrixStack, int offsetX, int offsetY, Rect2i srcRect) {
        drawTexture(matrixStack, offsetX, offsetY, srcRect.getX(), srcRect.getY(), srcRect.getWidth(), srcRect.getHeight());
    }
}