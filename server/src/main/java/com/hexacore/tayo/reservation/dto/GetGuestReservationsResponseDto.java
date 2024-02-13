package com.hexacore.tayo.reservation.dto;

import com.hexacore.tayo.reservation.model.GuestReservation;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GetGuestReservationsResponseDto {

    private final List<GuestReservation> reservations;
}
