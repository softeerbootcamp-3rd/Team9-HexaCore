package com.hexacore.tayo.reservation.model;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GuestReservationListDto {

    private final List<GuestReservationDto> reservations;
}
