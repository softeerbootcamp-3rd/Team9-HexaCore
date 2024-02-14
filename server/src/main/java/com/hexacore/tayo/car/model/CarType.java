package com.hexacore.tayo.car.model;

public enum CarType {
    LIGHT("경차"),
    SMALL("소형차"),
    SEMI_MIDDLE("준중형차"),
    MIDDLE("중형차"),
    HEAVY("대형차"),
    SUV("SUV"),

    RV("캠핑카"),
    VAN("VAN"),
    NOT_FOUND("not found");
    private final String value;

    CarType(String value) {
        this.value = value;
    }

    public String getType() {
        return value;
    }

    public static CarType of(String carType) {
        for (CarType c : values()) {
            if (c.value.equals(carType)) {
                return c;
            }
        }
        return NOT_FOUND;
    }
}
