package com.hexacore.tayo.car.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetPresignedUrlsResposneDto {
    private final String originalPresignedUrl;
    private final String downscaledPresignedURl;
}
