package com.hexacore.tayo.car.dto;

import com.hexacore.tayo.car.dto.GetCarDateRangeResponseDto.CarDateRangeListDto;
import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.car.model.CarImage;
import com.hexacore.tayo.user.dto.GetUserSimpleResponseDto;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class GetCarResponseDto {

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
    private final List<List<String>> carDateRanges = new ArrayList<>();

    private final String description;

    private GetCarResponseDto(Car car) {
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
        this.carDateRanges = car.getCarDateRanges();
        this.host = new GetUserSimpleResponseDto(car.getOwner());
    }

    public static GetCarResponseDto of(Car car) {
        return new GetCarResponseDto(car);
    }

    // FIXME: duplicate method
    public GetCarResponseDto(Car car, List<String> images) {
        this.carName = car.getSubcategory().getName();
        this.carNumber = car.getCarNumber();
        this.imageUrls = images;
        this.mileage = car.getMileage();
        this.type = car.getType().getValue();
        this.fuel = car.getFuel().getValue();
        this.capacity = car.getCapacity();
        this.year = car.getYear();
        this.feePerHour = car.getFeePerHour();
        this.address = car.getAddress();
        this.description = car.getDescription();
        this.carDateRanges.addAll(new CarDateRangeListDto(car.getCarDateRanges()).getDateRanges());
        this.host = new GetUserSimpleResponseDto(car.getOwner());
    }
}
