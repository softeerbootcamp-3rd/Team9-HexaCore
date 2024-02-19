package com.hexacore.tayo.reservation.dto;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GetGuestReservationListResponseDto {

    private final List<GetGuestReservationResponseDto> reservations;

    @Override
    public String toString() {
        return "{"
                + "reservations=" + reservations
                + '}';
    }
}
