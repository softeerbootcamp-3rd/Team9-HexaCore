package com.hexacore.tayo.car.model;

import com.hexacore.tayo.common.Position;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class SearchCarsDto {
    Double distance; // unit: meters
    Position position;
    LocalDate rentDate;
    LocalDate returnDate;
    Integer people;
    CarType type;
    Integer categoryId;
    Integer subcategoryId;
    Integer minPrice;
    Integer maxPrice;
}
