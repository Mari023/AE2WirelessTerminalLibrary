package de.mari_023.ae2wtlib.trinket;

import appeng.menu.SlotSemantic;
import de.mari_023.ae2wtlib.AE2wtlibSlotSemantics;
import de.mari_023.ae2wtlib.wct.WCTMenu;
import dev.emi.trinkets.api.SlotGroup;
import dev.emi.trinkets.api.TrinketInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrinketsSlots {
    private final WCTMenu menu;
    private final List<AppEngTrinketSlot> slots = new ArrayList<>();

    public TrinketsSlots(WCTMenu menu) {
        this.menu = menu;

        var inventory = TrinketsHelper.getTrinketsInventory(menu.getPlayer());
        if (inventory == null) return;

        for (Map.Entry<String, Map<String, TrinketInventory>> slotTypes : inventory.entrySet()) {
            for (Map.Entry<String, TrinketInventory> slots : slotTypes.getValue().entrySet()) {
                var group = slots.getValue().getComponent().getGroups().get(slots.getValue().getSlotType().getGroup());
                var trinkets = new TrinketInventoryWrapper(slots.getValue());
                for (int i = 0; i < trinkets.size(); i++) {
                    addSlot(new AppEngTrinketSlot(trinkets, i, group, slots.getValue().getSlotType(), i, false), getSlotSemantic(group));
                }
            }
        }
    }

    public void repositionSlots() {
        for (AppEngTrinketSlot slot : slots) {
            if (slot.slotOffset % 2 == 0)
                slot.x = slot.x - 18 * ((slot.slotOffset + 1) / 2);
            else
                slot.x = slot.x + 18 * (slot.slotOffset / 2);
        }
    }

    private void addSlot(AppEngTrinketSlot slot, SlotSemantic semantic) {
        slots.add(slot);
        menu.addTrinketSlot(slot, semantic);
    }

    private SlotSemantic getSlotSemantic(SlotGroup group) {
        return switch (group.getName()) {
            case "head" -> AE2wtlibSlotSemantics.TRINKETS_HELMET;
            case "chest" -> AE2wtlibSlotSemantics.TRINKETS_CHESTPLATE;
            case "legs" -> AE2wtlibSlotSemantics.TRINKETS_LEGGINGS;
            case "feet" -> AE2wtlibSlotSemantics.TRINKETS_BOOTS;
            case "hand" -> AE2wtlibSlotSemantics.TRINKETS_MAINHAND;
            case "offhand" -> AE2wtlibSlotSemantics.TRINKETS_OFFHAND;
            default -> AE2wtlibSlotSemantics.TRINKETS_EXTRA;
        };
    }
}
