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
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class WITScreen extends AEBaseScreen<WITContainer> implements IUniversalTerminalCapable {

    private static final int GUI_WIDTH = 195;

    private static final int GUI_PADDING_X = 8;
    private static final int GUI_PADDING_Y = 6;
    private static final int GUI_BUTTON_X_MARGIN = -18;
    private static final int GUI_BUTTON_Y_MARGIN = 8;

    private static final int GUI_HEADER_HEIGHT = 17;
    private static final int GUI_FOOTER_HEIGHT = 97;

    /**
     * Margin in pixel of a header text after the previous element.
     */
    private static final int HEADER_TEXT_MARGIN_Y = 3;

    /**
     * Additional margin in pixel for a text row inside the scrolling box.
     */
    private static final int INTERFACE_NAME_MARGIN_X = 2;

    /**
     * The maximum length for the string of a text row in pixel.
     */
    private static final int TEXT_MAX_WIDTH = 155;

    /**
     * Height of a table-row in pixels.
     */
    private static final int ROW_HEIGHT = 18;

    /**
     * Number of rows for a normal terminal (not tall)
     */
    private static final int DEFAULT_ROW_COUNT = 6;
    /**
     * Minimum rows for a tall terminal. Should prevent some strange aspect ratios from not displaying any rows.
     */
    private static final int MIN_ROW_COUNT = 3;

    /**
     * Size of a slot in both x and y dimensions in pixel, most likely always the same as ROW_HEIGHT.
     */
    private static final int SLOT_SIZE = ROW_HEIGHT;

    // Bounding boxes of key areas in the UI texture.
    // The upper part of the UI, anything above the scrollable area (incl. its top border)
    private static final Rect2i HEADER_BBOX = new Rect2i(0, 0, GUI_WIDTH, GUI_HEADER_HEIGHT);
    // Background for a text row in the scroll-box.
    // Spans across the whole texture including the right and left borders including the scrollbar.
    // Covers separate textures for the top, middle and bottoms rows for more customization.
    private static final Rect2i ROW_TEXT_TOP_BBOX = new Rect2i(0, 17, GUI_WIDTH, ROW_HEIGHT);
    private static final Rect2i ROW_TEXT_MIDDLE_BBOX = new Rect2i(0, 53, GUI_WIDTH, ROW_HEIGHT);
    private static final Rect2i ROW_TEXT_BOTTOM_BBOX = new Rect2i(0, 89, GUI_WIDTH, ROW_HEIGHT);
    // Background for a inventory row in the scroll-box.
    // Spans across the whole texture including the right and left borders including the scrollbar.
    // Covers separate textures for the top, middle and bottoms rows for more customization.
    private static final Rect2i ROW_INVENTORY_TOP_BBOX = new Rect2i(0, 35, GUI_WIDTH, ROW_HEIGHT);
    private static final Rect2i ROW_INVENTORY_MIDDLE_BBOX = new Rect2i(0, 71, GUI_WIDTH, ROW_HEIGHT);
    private static final Rect2i ROW_INVENTORY_BOTTOM_BBOX = new Rect2i(0, 107, GUI_WIDTH, ROW_HEIGHT);
    // This is the lower part of the UI, anything below the scrollable area (incl. its bottom border)
    private static final Rect2i FOOTER_BBOX = new Rect2i(0, 125, GUI_WIDTH, GUI_FOOTER_HEIGHT);

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
        final Scrollbar scrollbar = new Scrollbar();
        setScrollBar(scrollbar);
        backgroundWidth = GUI_WIDTH;
    }

    @Override
    public void init() {
        // Decide on number of rows.
        TerminalStyle terminalStyle = AEConfig.instance().getTerminalStyle();
        int maxLines = terminalStyle == TerminalStyle.SMALL ? DEFAULT_ROW_COUNT : Integer.MAX_VALUE;
        numLines = (height - GUI_HEADER_HEIGHT - GUI_FOOTER_HEIGHT) / ROW_HEIGHT;
        numLines = MathHelper.clamp(numLines, MIN_ROW_COUNT, maxLines);
        // Render inventory in correct place.
        backgroundHeight = GUI_HEADER_HEIGHT + GUI_FOOTER_HEIGHT + numLines * ROW_HEIGHT;

        super.init();
        searchField = new AETextField(textRenderer, x + 104, y + 4, 65, 12);
        searchField.setDrawsBackground(false);
        searchField.setMaxLength(25);
        searchField.setEditableColor(0xFFFFFF);
        searchField.setVisible(true);
        searchField.setChangedListener(str -> refreshList());
        addChild(searchField);
        changeFocus(true);

        // Add a terminalstyle button
        int offset = y + GUI_BUTTON_Y_MARGIN;
        addButton(new SettingToggleButton<>(x + GUI_BUTTON_X_MARGIN, offset, Settings.TERMINAL_STYLE, terminalStyle, this::toggleTerminalStyle));

        // Reposition player inventory slots.
        for(final Slot s : handler.slots)
            if(s instanceof AppEngSlot) ((SlotMixin) s).setY(((AppEngSlot) s).getY() + backgroundHeight - 82);
        // numLines may have changed, recalculate scroll bar.
        resetScrollbar();
        if(handler.isWUT()) addButton(new CycleTerminalButton(x - 18, offset + 20, btn -> cycleTerminal()));
    }

    @Override
    public void drawFG(MatrixStack matrixStack, final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        textRenderer.draw(matrixStack, getGuiDisplayName(GuiText.InterfaceTerminal.text()).getString(), GUI_PADDING_X, GUI_PADDING_Y, COLOR_DARK_GRAY);

        handler.slots.removeIf(slot -> slot instanceof SlotDisconnected);

        final int scrollLevel = getScrollBar().getCurrentScroll();
        int i = 0;
        for(; i < numLines; ++i)
            if(scrollLevel + i < lines.size()) {
                final Object lineObj = lines.get(scrollLevel + i);
                if(lineObj instanceof ClientDCInternalInv) {
                    // Note: We have to shift everything after the header up by 1 to avoid black line duplication.
                    final ClientDCInternalInv inv = (ClientDCInternalInv) lineObj;
                    for(int row = 0; row < inv.getInventory().getSlotCount(); row++)
                        handler.slots.add(new SlotDisconnected(inv, row, row * SLOT_SIZE + GUI_PADDING_X, (i + 1) * SLOT_SIZE));
                } else if(lineObj instanceof String) {
                    String name = (String) lineObj;
                    final int rows = byName.get(name).size();
                    if(rows > 1) name = name + " (" + rows + ')';

                    name = textRenderer.trimToWidth(name, TEXT_MAX_WIDTH, true);

                    textRenderer.draw(matrixStack, name, GUI_PADDING_X + INTERFACE_NAME_MARGIN_X, GUI_PADDING_Y + GUI_HEADER_HEIGHT + i * ROW_HEIGHT, COLOR_DARK_GRAY);
                }
            }
        textRenderer.draw(matrixStack, GuiText.inventory.text().getString(), GUI_PADDING_X, HEADER_TEXT_MARGIN_Y + GUI_HEADER_HEIGHT + i * ROW_HEIGHT, COLOR_DARK_GRAY);
    }

    @Override
    public boolean mouseClicked(final double xCoord, final double yCoord, final int btn) {
        if(searchField.mouseClicked(xCoord, yCoord, btn)) return true;

        if(btn == 1 && searchField.isMouseOver(xCoord, yCoord)) {
            searchField.setText("");
            return true;
        }
        return super.mouseClicked(xCoord, yCoord, btn);
    }

    @Override
    public void drawBG(MatrixStack matrixStack, final int offsetX, final int offsetY, final int mouseX, final int mouseY, float partialTicks) {
        bindTexture("wtlib/gui/interface.png");

        // Draw the top of the dialog
        blit(matrixStack, offsetX, offsetY, HEADER_BBOX);

        final int scrollLevel = getScrollBar().getCurrentScroll();
        boolean isInvLine;

        int currentY = offsetY + GUI_HEADER_HEIGHT;

        // Draw the footer now so slots will draw on top of it
        blit(matrixStack, offsetX, currentY + numLines * ROW_HEIGHT, FOOTER_BBOX);

        for(int i = 0; i < numLines; ++i) {
            // Draw the dialog background for this row
            // Skip 1 pixel for the first row in order to not over-draw on the top scrollbox border,
            // and do the same but for the bottom border on the last row
            boolean firstLine = i == 0;
            boolean lastLine = i == numLines - 1;

            // Draw the background for the slots in an inventory row
            isInvLine = false;
            if(scrollLevel + i < lines.size()) {
                final Object lineObj = lines.get(scrollLevel + i);
                isInvLine = lineObj instanceof ClientDCInternalInv;
            }

            Rect2i bbox = selectRowBackgroundBox(isInvLine, firstLine, lastLine);
            blit(matrixStack, offsetX, currentY, bbox);

            currentY += ROW_HEIGHT;
        }
        // Draw search field.
        if(searchField != null) searchField.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private Rect2i selectRowBackgroundBox(boolean isInvLine, boolean firstLine, boolean lastLine) {
        if(isInvLine) {
            if(firstLine) return ROW_INVENTORY_TOP_BBOX;
            else if(lastLine) return ROW_INVENTORY_BOTTOM_BBOX;
            else return ROW_INVENTORY_MIDDLE_BBOX;
        } else {
            if(firstLine) return ROW_TEXT_TOP_BBOX;
            else if(lastLine) return ROW_TEXT_BOTTOM_BBOX;
            else return ROW_TEXT_MIDDLE_BBOX;
        }
    }

    @Override
    public boolean charTyped(char character, int key) {
        if(character == ' ' && searchField.getText().isEmpty()) return true;
        return super.charTyped(character, key);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int p_keyPressed_3_) {
        InputUtil.Key input = InputUtil.fromKeyCode(keyCode, scanCode);

        if(keyCode != GLFW.GLFW_KEY_ESCAPE) {
            if(AppEng.instance().isActionKey(ActionKey.TOGGLE_FOCUS, input)) {
                searchField.setTextFieldFocused(!searchField.isFocused());
                return true;
            }

            // Forward keypresses to the search field
            if(searchField.isFocused()) {
                if(keyCode == GLFW.GLFW_KEY_ENTER) {
                    searchField.setTextFieldFocused(false);
                    return true;
                }

                searchField.keyPressed(keyCode, scanCode, p_keyPressed_3_);

                // We need to swallow key presses if the field is focused because typing 'e'
                // would otherwise close the screen
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, p_keyPressed_3_);
    }

    public void postUpdate(final CompoundTag in) {
        if(in.getBoolean("clear")) {
            byId.clear();
            refreshList = true;
        }

        for(final String oKey : in.getKeys()) {
            if(oKey.startsWith("=")) try {
                final long id = Long.parseLong(oKey.substring(1), Character.MAX_RADIX);
                final CompoundTag invData = in.getCompound(oKey);
                Text un = Text.Serializer.fromJson(invData.getString("un"));
                final ClientDCInternalInv current = getById(id, invData.getLong("sortBy"), un);

                for(int x = 0; x < current.getInventory().getSlotCount(); x++) {
                    final String which = Integer.toString(x);
                    if(invData.contains(which))
                        current.getInventory().setInvStack(x, ItemStack.fromTag(invData.getCompound(which)), Simulation.ACTION);
                }
            } catch(final NumberFormatException ignored) { }
        }

        if(refreshList) {
            refreshList = false;
            // invalid caches on refresh
            cachedSearches.clear();
            refreshList();
        }
    }

    /**
     * Rebuilds the list of interfaces.
     * <p>
     * Respects a search term if present (ignores case) and adding only matching patterns.
     */
    private void refreshList() {
        byName.clear();

        final String searchFilterLowerCase = searchField.getText().toLowerCase();

        final Set<Object> cachedSearch = getCacheForSearchTerm(searchFilterLowerCase);
        final boolean rebuild = cachedSearch.isEmpty();

        for(final ClientDCInternalInv entry : byId.values()) {
            // ignore inventory if not doing a full rebuild or cache already marks it as miss.
            if(!rebuild && !cachedSearch.contains(entry)) continue;

            // Shortcut to skip any filter if search term is ""/empty
            boolean found = searchFilterLowerCase.isEmpty();

            // Search if the current inventory holds a pattern containing the search term.
            if(!found) for(final ItemStack itemStack : entry.getInventory()) {
                found = itemStackMatchesSearchTerm(itemStack, searchFilterLowerCase);
                if(found) break;
            }

            // if found, filter skipped or machine name matching the search term, add it
            if(found || entry.getSearchName().contains(searchFilterLowerCase)) {
                byName.put(entry.getFormattedName(), entry);
                cachedSearch.add(entry);
            } else cachedSearch.remove(entry);
        }

        names.clear();
        names.addAll(byName.keySet());

        Collections.sort(names);

        lines.clear();
        lines.ensureCapacity(getMaxRows());

        for(final String n : names) {
            lines.add(n);

            List<ClientDCInternalInv> clientInventories = new ArrayList<>(byName.get(n));

            Collections.sort(clientInventories);
            lines.addAll(clientInventories);
        }
        // lines may have changed - recalculate scroll bar.
        resetScrollbar();
    }

    /**
     * Should be called whenever lines.size() or numLines changes.
     */
    private void resetScrollbar() {
        Scrollbar bar = getScrollBar();
        // Needs to take the border into account, so offset for 1 px on the top and bottom.
        bar.setLeft(175).setTop(GUI_HEADER_HEIGHT + 1).setHeight(numLines * ROW_HEIGHT - 2);
        bar.setRange(0, lines.size() - numLines, 2);
    }

    private boolean itemStackMatchesSearchTerm(final ItemStack itemStack, final String searchTerm) {
        if(itemStack.isEmpty()) return false;

        final CompoundTag encodedValue = itemStack.getTag();

        if(encodedValue == null) return false;

        // Potential later use to filter by input
        // ListNBT inTag = encodedValue.getTagList( "in", 10 );
        final ListTag outTag = encodedValue.getList("out", 10);

        for(int i = 0; i < outTag.size(); i++) {
            final ItemStack parsedItemStack = ItemStack.fromTag(outTag.getCompound(i));
            if(!parsedItemStack.isEmpty()) {
                final String displayName = Platform.getItemDisplayName(Api.instance().storage().getStorageChannel(IItemStorageChannel.class)
                        .createStack(parsedItemStack)).getString().toLowerCase();
                if(displayName.contains(searchTerm)) return true;
            }
        }
        return false;
    }

    /**
     * Tries to retrieve a cache for a with search term as keyword.
     * <p>
     * If this cache should be empty, it will populate it with an earlier cache if available or at least the cache for
     * the empty string.
     *
     * @param searchTerm the corresponding search
     * @return a Set matching a superset of the search term
     */
    private Set<Object> getCacheForSearchTerm(final String searchTerm) {
        if(!cachedSearches.containsKey(searchTerm)) cachedSearches.put(searchTerm, new HashSet<>());

        final Set<Object> cache = cachedSearches.get(searchTerm);

        if(cache.isEmpty() && searchTerm.length() > 1) {
            cache.addAll(getCacheForSearchTerm(searchTerm.substring(0, searchTerm.length() - 1)));
            return cache;
        }
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

    /**
     * The max amount of unique names and each inv row. Not affected by the filtering.
     *
     * @return max amount of unique names and each inv row
     */
    private int getMaxRows() {
        return names.size() + byId.size();
    }

    private ClientDCInternalInv getById(final long id, final long sortBy, final Text name) {
        ClientDCInternalInv o = byId.get(id);
        if(o == null) {
            byId.put(id, o = new ClientDCInternalInv(9, id, sortBy, name));
            refreshList = true;
        }
        return o;
    }

    /**
     * A version of blit that lets us pass a source rectangle
     */
    private void blit(MatrixStack matrixStack, int offsetX, int offsetY, Rect2i srcRect) {
        drawTexture(matrixStack, offsetX, offsetY, srcRect.getX(), srcRect.getY(), srcRect.getWidth(), srcRect.getHeight());
    }
}