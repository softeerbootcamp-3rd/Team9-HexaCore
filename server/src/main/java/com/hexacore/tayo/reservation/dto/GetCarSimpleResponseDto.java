package com.hexacore.tayo.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class GetCarSimpleResponseDto {

    private final Long id;
    private final String name;
    private final String imageUrl;
    private final Double lat;
    private final Double lng;

    @Override
    public String toString() {
        return "{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", imageUrl='" + imageUrl + '\''
                + ", lat=" + lat
                + ", lng=" + lng
                + '}';
    }
}
