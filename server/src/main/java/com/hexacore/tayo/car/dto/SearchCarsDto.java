package com.hexacore.tayo.car.dto;

import com.hexacore.tayo.car.model.CarType;
import com.hexacore.tayo.common.Position;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Builder
@Getter
public class SearchCarsDto {
    private final Double distance; // unit: meters
    private final Position position;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Integer party;
    private final List<CarType> types;
    private final Long categoryId;
    private final Long subcategoryId;
    private final Integer minPrice;
    private final Integer maxPrice;

    static public SearchCarsDto of(SearchCarsRequestDto searchCarsRequestDto) {
        List<CarType> types = searchCarsRequestDto.getType() == null
                ? new ArrayList<>()
                : Arrays.stream(searchCarsRequestDto.getType().split(","))
                .map(CarType::of)
                .toList();

        return SearchCarsDto.builder()
                .distance(searchCarsRequestDto.getDistance())
                .position(new Position(searchCarsRequestDto.getLat(), searchCarsRequestDto.getLng()))
                .startDate(searchCarsRequestDto.getStartDate())
                .endDate(searchCarsRequestDto.getEndDate())
                .party(searchCarsRequestDto.getParty())
                .types(types)
                .categoryId(searchCarsRequestDto.getCategoryId())
                .subcategoryId(searchCarsRequestDto.getSubcategoryId())
                .minPrice(searchCarsRequestDto.getMinPrice())
                .maxPrice(searchCarsRequestDto.getMaxPrice())
                .build();
    }
}
