package com.hexacore.tayo.car.dto;

import com.hexacore.tayo.car.dto.UpdateCarDateRangeDto.CarDateRangeDto;
import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.car.model.CarType;
import com.hexacore.tayo.user.dto.GetUserSimpleResponseDto;
import java.util.List;
import lombok.Getter;

@Getter
public class GetCarResponseDto {

    private GetUserSimpleResponseDto host;
    private String carName;
    private String carNumber;
    private List<String> imageUrls;
    private Double mileage;
    private String fuel;
    private CarType type;
    private Integer capacity;
    private Integer year;
    private Integer feePerHour;
    private String address;
    private String description;
    private List<CarDateRangeDto> carDateRanges;

    public GetCarResponseDto(Car car, List<CarDateRangeDto> carDateRanges, List<String> images) {
        this.carName = car.getSubCategory().getName();
        this.carNumber = car.getCarNumber();
        this.imageUrls = images;
        this.mileage = car.getMileage();
        this.fuel = car.getFuel().getType();
        this.type = car.getType();
        this.capacity = car.getCapacity();
        this.year = car.getYear();
        this.feePerHour = car.getFeePerHour();
        this.address = car.getAddress();
        this.description = car.getDescription();
        this.carDateRanges = carDateRanges;
        this.host = new GetUserSimpleResponseDto(car.getOwner());
    }
}
