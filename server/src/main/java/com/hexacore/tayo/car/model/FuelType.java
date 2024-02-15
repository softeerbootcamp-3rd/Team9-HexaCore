package com.hexacore.tayo.car.model;

public enum FuelType {
    // 휘발유/경유/전기/수소/LPG
    LPG("LPG"),
    HYDROGEN("수소"),
    ELECTRICITY("전기"),
    DIESEL("경유"),
    GASOLINE("휘발유"),
    NOT_FOUND("not found");

    private final String value;

    FuelType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static FuelType of(String fuelType) {
        for (FuelType f : values()) {
            if (f.value.equals(fuelType)) {
                return f;
            }
        }
        return NOT_FOUND;
    }
}
