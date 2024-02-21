package com.hexacore.tayo.reservation.model;

import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;

public enum ReservationStatus {
    USING("using"),
    READY("ready"),
    CANCEL("cancel"),
    TERMINATED("terminated");

    private final String status;

    ReservationStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static ReservationStatus getReservationStatus(String status) {
        for (ReservationStatus r : values()) {
            if (r.status.equals(status)) {
                return r;
            }
        }
        throw new GeneralException(ErrorCode.RESERVATION_STATUS_NOT_FOUND);
    }
}
