package com.hexacore.tayo.car.dto;

import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.car.model.CarDateRange;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class UpdateCarDateRangeDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CarDateRangeListDto {

        private List<CarDateRangeDto> carDateRanges;
    }

    @Getter
    @AllArgsConstructor
    public static class CarDateRangeDto {

        @NotNull
        private LocalDate startDate;
        @NotNull
        private LocalDate endDate;

        public CarDateRangeDto(CarDateRange carDateRange) {
            this.startDate = carDateRange.getStartDate();
            this.endDate = carDateRange.getEndDate();
        }

        public CarDateRange toEntity(Car car) {
            return CarDateRange.builder()
                    .car(car)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();
        }
    }
}
