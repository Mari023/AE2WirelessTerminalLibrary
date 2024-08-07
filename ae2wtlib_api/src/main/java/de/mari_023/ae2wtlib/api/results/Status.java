package de.mari_023.ae2wtlib.api.results;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;

import appeng.api.storage.ILinkStatus;
import appeng.core.localization.PlayerMessages;

import de.mari_023.ae2wtlib.api.TextConstants;

public enum Status {
    // Generic results
    Valid(null),
    NotServer(null),
    /**
     * Invalid Result where no error is displayed
     */
    GenericInvalid(null),
    // Grid results
    NotLinked(PlayerMessages.DeviceNotLinked.text()),
    NotPowered(TextConstants.NETWORK_NOT_POWERED),
    NotFound(PlayerMessages.LinkedNetworkNotFound.text()),
    // Quantum Bridge results
    NoUpgrade(TextConstants.NO_QNB_UPGRADE),
    BridgeNotFound(TextConstants.NO_QNB),
    DifferentNetworks(TextConstants.DIFFERENT_NETWORKS),
    // Singularity results
    NoSingularity(TextConstants.SINGULARITY_NOT_PRESENT);

    @Nullable
    public final Component error;

    Status(@Nullable Component error) {
        this.error = error;
    }

    public boolean isValid() {
        return this == Valid;
    }

    public ILinkStatus toILinkStatus() {
        if (isValid())
            return ILinkStatus.ofConnected();
        return ILinkStatus.ofDisconnected(error);
    }

    public boolean is(ILinkStatus status) {
        return status.statusDescription() == error;
    }
}
