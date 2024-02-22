package com.hexacore.tayo.car.dto;

import com.hexacore.tayo.car.model.CarType;
import com.hexacore.tayo.common.Position;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class SearchCarsDto {
    private final Double distance; // unit: meters
    private final Position position;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Integer party;
    private final CarType type;
    private final Long categoryId;
    private final Long subcategoryId;
    private final Integer minPrice;
    private final Integer maxPrice;

    static public SearchCarsDto of(SearchCarsRequestDto searchCarsRequestDto) {
        String type = searchCarsRequestDto.getType();
        return SearchCarsDto.builder()
                .distance(searchCarsRequestDto.getDistance())
                .position(new Position(searchCarsRequestDto.getLat(), searchCarsRequestDto.getLng()))
                .startDate(searchCarsRequestDto.getStartDate())
                .endDate(searchCarsRequestDto.getEndDate())
                .party(searchCarsRequestDto.getParty())
                .type(type == null ? null : CarType.valueOf(type))
                .categoryId(searchCarsRequestDto.getCategoryId())
                .subcategoryId(searchCarsRequestDto.getSubcategoryId())
                .minPrice(searchCarsRequestDto.getMinPrice())
                .maxPrice(searchCarsRequestDto.getMaxPrice())
                .build();
    }
}
