package com.hexacore.tayo.car.model;

import com.hexacore.tayo.common.Position;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class SearchCarsDto {
    double distance; // unit: meters
    Position position;
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
