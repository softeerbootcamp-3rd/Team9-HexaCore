package com.hexacore.tayo.car.model;

import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;

public enum FuelType {
    // 휘발유/경유/전기/수소/LPG
    LPG("LPG"),
    HYDROGEN("수소"),
    ELECTRICITY("전기"),
    DIESEL("경유"),
    GASOLINE("휘발유");

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
        throw new GeneralException(ErrorCode.FUEL_TYPE_NOT_FOUND);
    }
}
