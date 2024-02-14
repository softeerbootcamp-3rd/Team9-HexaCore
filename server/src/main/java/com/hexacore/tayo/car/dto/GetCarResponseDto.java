package com.hexacore.tayo.car.dto;

import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.car.model.CarType;
import com.hexacore.tayo.car.model.FuelType;
import com.hexacore.tayo.user.dto.GetUserSimpleResponseDto;
import java.time.LocalDateTime;
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
    private List<List<LocalDateTime>> dates;

    public GetCarResponseDto(Car car, List<String> images) {
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
        this.dates = car.getDates();
        this.host = new GetUserSimpleResponseDto(car.getOwner());
    }
}
