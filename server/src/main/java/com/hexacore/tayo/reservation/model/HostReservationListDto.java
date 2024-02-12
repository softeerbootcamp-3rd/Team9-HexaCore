package com.hexacore.tayo.reservation.model;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class HostReservationListDto {

    private final List<HostReservationDto> reservations;
}
