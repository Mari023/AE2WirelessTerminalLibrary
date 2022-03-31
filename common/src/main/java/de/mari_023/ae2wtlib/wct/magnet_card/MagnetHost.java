package de.mari_023.ae2wtlib.wct.magnet_card;

import appeng.api.config.FuzzyMode;
import appeng.api.config.IncludeExclude;
import appeng.util.ConfigInventory;
import appeng.util.prioritylist.IPartitionList;

public class MagnetHost {

    public final ConfigInventory pickupConfig = ConfigInventory.configTypes(null,
            27, this::updatePickupFilter);
    public final ConfigInventory insertConfig = ConfigInventory.configTypes(null,
            27, this::updateInsertFilter);

    private IPartitionList pickupFilter = createFilter(pickupConfig);
    private IPartitionList insertFilter = createFilter(insertConfig);

    private IncludeExclude pickupMode = IncludeExclude.WHITELIST;// TODO load on create
    private IncludeExclude insertMode = IncludeExclude.WHITELIST;

    private IPartitionList createFilter(ConfigInventory config) {
        IPartitionList.Builder builder = IPartitionList.builder();
        builder.fuzzyMode(FuzzyMode.IGNORE_ALL);
        for (int x = 0; x < config.size(); x++) {
            builder.add(config.getKey(x));
        }
        return builder.build();
    }

    private void updatePickupFilter() {
        pickupFilter = createFilter(pickupConfig);
    }

    private void updateInsertFilter() {
        insertFilter = createFilter(insertConfig);
    }

    public IPartitionList getPickupFilter() {
        return pickupFilter;
    }

    public IncludeExclude getPickupMode() {
        return pickupMode;
    }

    public void setPickupMode(IncludeExclude pickupMode) {
        this.pickupMode = pickupMode;
    }

    public IPartitionList getInsertFilter() {
        return insertFilter;
    }

    public IncludeExclude getInsertMode() {
        return insertMode;
    }

    public void setInsertMode(IncludeExclude insertMode) {
        this.insertMode = insertMode;
    }
}
