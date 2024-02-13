package com.hexacore.tayo.car.dto;

import com.hexacore.tayo.common.Position;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Getter
@AllArgsConstructor
public class UpdateCarRequestDto {

    private Integer feePerHour;
    private String address;
    private Position position;
    private String description;
    private List<MultipartFile> imageFiles;
    private List<Integer> imageIndexes;
}
