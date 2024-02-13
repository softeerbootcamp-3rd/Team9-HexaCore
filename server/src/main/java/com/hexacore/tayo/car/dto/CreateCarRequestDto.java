package com.hexacore.tayo.car.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class CreateCarRequestDto {

    @NotNull
    private String carNumber;
    @NotNull
    private String carName;
    @NotNull
    private Double mileage;
    @NotNull
    private String fuel;
    @NotNull
    private String type;
    @NotNull
    private Integer capacity;
    @NotNull
    private Integer year;
    @NotNull
    private Integer feePerHour;
    @NotNull
    private String address;
    @NotNull
    private CreatePositionRequestDto position;
    private String description;
    @NotNull
    private List<MultipartFile> imageFiles;
    @NotNull
    private List<Integer> imageIndexes;

}
