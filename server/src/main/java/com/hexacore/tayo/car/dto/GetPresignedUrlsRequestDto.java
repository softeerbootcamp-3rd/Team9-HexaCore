package com.hexacore.tayo.car.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetPresignedUrlsRequestDto {
    @NotNull
    private final String fileName;
    @NotNull
    private final String fileType;
    private final String prefix;
}
