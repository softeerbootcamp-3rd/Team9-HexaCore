package com.hexacore.tayo.car.dto;

import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.car.model.CarDateRange;
import com.hexacore.tayo.car.model.CarImage;
import com.hexacore.tayo.reservation.model.Reservation;
import com.hexacore.tayo.user.dto.GetUserSimpleResponseDto;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class GetCarResponseDto {

    @NotNull
    private final Long id;

    @NotNull
    private final GetUserSimpleResponseDto host;

    @NotNull
    private final String carName;

    @NotNull
    private final String carNumber;

    @NotNull
    private final List<String> imageUrls;

    @NotNull
    private final Double mileage;

    @NotNull
    private final String fuel;

    @NotNull
    private final String type;

    @NotNull
    private final Integer capacity;

    @NotNull
    private final Integer year;

    @NotNull
    private final Integer feePerHour;

    @NotNull
    private final String address;

    @NotNull
    private final List<List<String>> carDateRanges;

    private final String description;

    private GetCarResponseDto(Car car) {
        this.id = car.getId();
        this.carName = car.getSubcategory().getName();
        this.carNumber = car.getCarNumber();
        this.imageUrls = car.getCarImages().stream().map(CarImage::getUrl).toList();
        this.mileage = car.getMileage();
        this.fuel = car.getFuel().getValue();
        this.type = car.getType().getValue();
        this.capacity = car.getCapacity();
        this.year = car.getYear();
        this.feePerHour = car.getFeePerHour();
        this.address = car.getAddress();
        this.description = car.getDescription();
        this.carDateRanges = getCarAvailableDates(car.getCarDateRanges());
        this.host = new GetUserSimpleResponseDto(car.getOwner());
    }

    public static GetCarResponseDto of(Car car) {
        return new GetCarResponseDto(car);
    }

    private List<List<String>> getCarAvailableDates(List<CarDateRange> CarDateRanges) {
        List<List<String>> carAvailableDates = new ArrayList<>();
        for (CarDateRange carDateRange : CarDateRanges) {
            LocalDate start = carDateRange.getStartDate();
            LocalDate end;
            List<Reservation> sortedReservations = carDateRange.getReservations().stream()
                    .sorted(Comparator.comparing(Reservation::getRentDateTime))
                    .toList();
            for (Reservation reservation : sortedReservations) {
                end = reservation.getRentDateTime().toLocalDate().minusDays(1);
                if (!start.isAfter(end)) {
                    carAvailableDates.add(List.of(start.toString(), end.toString()));
                }
                start = reservation.getReturnDateTime().toLocalDate().plusDays(1);
            }
            if (!start.isAfter(carDateRange.getEndDate())) {
                carAvailableDates.add(List.of(start.toString(), carDateRange.getEndDate().toString()));
            }
        }
        return carAvailableDates.stream().sorted(Comparator.comparing(list -> list.get(0)))
                .collect(Collectors.toList());
    }
}
