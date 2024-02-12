package com.hexacore.tayo.reservation.model;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class HostCarDto {

    private final Long id;
    private final String name;
    private final String imageUrl;
}
