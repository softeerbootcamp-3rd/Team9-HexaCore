package com.hexacore.tayo.car.dto;

import com.hexacore.tayo.car.model.CarDateRange;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetCarDateRangeRequestDto {

    private List<CarDateRange> carDateRanges;
}
