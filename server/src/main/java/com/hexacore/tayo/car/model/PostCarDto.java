package com.hexacore.tayo.car.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
public class PostCarDto {

    private String carNumber;
    private String carName;
    private Double mileage;
    private String fuel;
    private String type;
    private Integer capacity;
    private Integer year;
    private Integer feePerHour;
    private String address;
    private PositionDto position;
    private String description;
    private List<MultipartFile> images;

}
