package com.hexacore.tayo.car.dto;

import com.hexacore.tayo.common.Position;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Getter
@AllArgsConstructor
public class UpdateCarRequestDto {

    @NotNull
    private Integer feePerHour;

    @NotNull
    private String address;

    @NotNull
    private Position position;

    @NotNull
    private String description;

    @NotNull
    private List<MultipartFile> imageFiles;

    @NotNull
    private List<Integer> imageIndexes;
}
