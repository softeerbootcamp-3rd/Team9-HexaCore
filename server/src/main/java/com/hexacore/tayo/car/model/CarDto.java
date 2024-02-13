package com.hexacore.tayo.car.model;

import com.hexacore.tayo.user.model.UserSimpleDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class CarDto {

    private UserSimpleDto host;
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

    public CarDto(CarEntity car, List<String> images) {
        this.carName = car.getModel().getSubCategory();
        this.carNumber = car.getCarNumber();
        this.imageUrls = images;
        this.mileage = car.getMileage();
        this.fuel = car.getFuel();
        this.type = car.getType();
        this.capacity = car.getCapacity();
        this.year = car.getYear();
        this.feePerHour = car.getFeePerHour();
        this.address = car.getAddress();
        this.description = car.getDescription();
        this.dates = car.getDates();
        this.host = new UserSimpleDto(car.getOwner());
    }
}
