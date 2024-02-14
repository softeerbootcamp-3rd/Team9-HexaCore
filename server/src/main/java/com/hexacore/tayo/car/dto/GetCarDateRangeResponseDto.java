package com.hexacore.tayo.car.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GetCarDateRangeResponseDto {

    private final LocalDate startDate;
    private final LocalDate endDate;
}
