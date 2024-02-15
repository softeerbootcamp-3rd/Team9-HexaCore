package com.hexacore.tayo.car.dto;

import com.hexacore.tayo.car.model.CarType;
import com.hexacore.tayo.common.Position;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class SearchCarsParamsDto {
    Double distance; // unit: meters
    Position position;
    LocalDate startDate;
    LocalDate endDate;
    Integer party;
    CarType type;
    Integer categoryId;
    Integer subcategoryId;
    Integer minPrice;
    Integer maxPrice;

    public Boolean hasAllRequiredParams() {
        return !(distance == null || position == null || startDate == null || endDate == null || party == null);
    }
}
