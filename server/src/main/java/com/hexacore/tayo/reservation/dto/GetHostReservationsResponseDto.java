package com.hexacore.tayo.reservation.dto;

import com.hexacore.tayo.reservation.model.HostReservation;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class GetHostReservationsResponseDto {

    private final List<HostReservation> reservations;
}
