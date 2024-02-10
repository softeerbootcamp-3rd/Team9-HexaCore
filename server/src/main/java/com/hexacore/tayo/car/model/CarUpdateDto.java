package com.hexacore.tayo.car.model;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Getter
@AllArgsConstructor
public class CarUpdateDto {
    private Integer feePerHour;
    private String address;
    private PositionDto position;
    private String description;
    private List<MultipartFile> imageFiles;
    private List<Integer> imageIndexes;
}
