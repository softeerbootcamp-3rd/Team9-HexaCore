package com.hexacore.tayo.car.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class SearchCarsDto {
    double distance; // unit: meters
    PositionDto position;
    LocalDateTime rentDate;
    LocalDateTime returnDate;
    int people;
    CarType type;
    String model;
    String category; // TODO: categoryId
    int subCategoryId;
    int minPrice;
    int maxPrice;
}
