package com.hexacore.tayo.car.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.car.model.CarDateRange;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class UpdateCarDateRangeRequestDto {

    @Getter
    @NoArgsConstructor
    public static class CarDateRangesDto {

        private List<List<LocalDate>> dates = new ArrayList<>();

        public CarDateRangesDto(List<CarDateRange> carDateRangeList) {
            carDateRangeList.stream()
                    .map(CarDateRangeDto::new)
                    .map(dateRange -> List.of(dateRange.startDate, dateRange.endDate))
                    .forEach(this.dates::add);
        }
    }

    @Getter
    @AllArgsConstructor
    public static class CarDateRangeDto {

        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;

        public CarDateRangeDto(CarDateRange carDateRange) {
            this.startDate = carDateRange.getStartDate();
            this.endDate = carDateRange.getEndDate();
        }

        public CarDateRangeDto(List<LocalDate> localDates) {
            this.startDate = localDates.get(0);
            this.endDate = localDates.get(1);
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
