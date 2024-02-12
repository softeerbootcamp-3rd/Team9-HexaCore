package com.hexacore.tayo.reservation.model;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class GuestDto {

    private final Long id;
    private final String nickname;
    private final String phoneNumber;
    private final String image;
}
