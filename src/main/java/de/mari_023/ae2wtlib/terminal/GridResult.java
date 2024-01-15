package de.mari_023.ae2wtlib.terminal;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;

import appeng.api.networking.IGrid;
import appeng.core.localization.PlayerMessages;

import de.mari_023.ae2wtlib.TextConstants;

public record GridResult(@Nullable IGrid grid, GridStatus status) {

    public static GridResult valid(IGrid grid) {
        return new GridResult(grid, GridStatus.Valid);
    }

    public static GridResult invalid(GridStatus status) {
        return new GridResult(null, status);
    }

    public enum GridStatus {
        Valid(null),
        NotServer(null),
        NotLinked(PlayerMessages.DeviceNotLinked.text()),
        NotPowered(TextConstants.NETWORK_NOT_POWERED),
        NotFound(PlayerMessages.LinkedNetworkNotFound.text());

        @Nullable
        private final Component error;

        GridStatus(@Nullable Component error) {
            this.error = error;
        }

        @Nullable
        public Component getError() {
            return error;
        }

        public boolean isValid() {
            return this == Valid;
        }
    }
}
