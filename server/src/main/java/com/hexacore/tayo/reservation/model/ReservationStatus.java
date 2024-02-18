package com.hexacore.tayo.reservation.model;

public enum ReservationStatus {
    CANCEL("cancel"),
    READY("ready"),
    USING("using"),
    TERMINATED("terminated"),
    NOT_FOUND("not found");

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

        return NOT_FOUND;
    }
}
