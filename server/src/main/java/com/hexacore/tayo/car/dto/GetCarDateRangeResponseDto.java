package com.hexacore.tayo.car.dto;

import com.hexacore.tayo.car.model.CarDateRange;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class GetCarDateRangeResponseDto {

    @Getter
    public static class CarDateRangeListDto {

        private final List<List<String>> dateRanges = new ArrayList<>();

        public CarDateRangeListDto(List<CarDateRange> dateRanges) {
            dateRanges.stream()
                    .map(CarDateRangeDto::new) // CarDateRange 객체를 CarDateRangeDto 객체로 변환
                    .map(CarDateRangeDto::getCarDateRange) // CarDateRangeDto 객체에서 carDateRange 리스트를 추출
                    .forEach(this.dateRanges::add);
        }
    }

    @Getter
    private static class CarDateRangeDto {

        private final List<String> carDateRange = new ArrayList<>();

        public CarDateRangeDto(CarDateRange carDateRange) {
            this.carDateRange.add(carDateRange.getStartDate().toString());
            this.carDateRange.add(carDateRange.getEndDate().toString());
        }
    }
}
