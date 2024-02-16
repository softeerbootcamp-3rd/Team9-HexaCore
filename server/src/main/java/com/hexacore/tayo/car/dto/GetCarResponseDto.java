package com.hexacore.tayo.car.dto;

import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.car.model.CarDateRange;
import com.hexacore.tayo.car.model.CarImage;
import com.hexacore.tayo.reservation.model.Reservation;
import com.hexacore.tayo.user.dto.GetUserSimpleResponseDto;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
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
        this.carDateRanges = car.getCarDateRanges().stream().reduce(new ArrayList<>(), (acc, carDateRange) -> {
            acc.add(List.of(carDateRange.getStartDate().toString(), carDateRange.getEndDate().toString()));
            return acc;
        }, (acc, carDateRange) -> acc);
        this.host = new GetUserSimpleResponseDto(car.getOwner());
    }

    public static GetCarResponseDto of(Car car) {
        return new GetCarResponseDto(car);
    }
}
