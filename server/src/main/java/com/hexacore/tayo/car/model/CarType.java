package com.hexacore.tayo.car.model;

import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;

public enum CarType {
    LIGHT("경차"),
    SMALL("소형차"),
    SEMI_MIDDLE("준중형차"),
    MIDDLE("중형차"),
    HEAVY("대형차"),
    SUV("SUV"),

    RV("캠핑카"),
    VAN("VAN");
    private final String value;

    CarType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CarType of(String carType) {
        for (CarType c : values()) {
            if (c.value.equals(carType)) {
                return c;
            }
        }
        throw new GeneralException(ErrorCode.CAR_TYPE_NOT_FOUND);
    }
}
