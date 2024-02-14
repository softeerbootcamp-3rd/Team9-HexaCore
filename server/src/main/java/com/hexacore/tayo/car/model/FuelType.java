package com.hexacore.tayo.car.model;

public enum FuelType {
    // 휘발유/경유/전기/수소/LPG
    LPG("LPG"),
    HYDROGEN("수소"),
    ELECTRICITY("전기"),
    DIESEL("경유"),
    GASOLINE("휘발유"),
    NOT_FOUND("not found");

    private final String type;

    FuelType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static FuelType of(String fuelType) {
        for (FuelType f : values()) {
            if (f.type.equals(fuelType)) {
                return f;
            }
        }
        return NOT_FOUND;
    }
}
