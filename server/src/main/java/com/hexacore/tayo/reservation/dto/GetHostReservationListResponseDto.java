package com.hexacore.tayo.reservation.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class GetHostReservationListResponseDto {

    private final List<GetHostReservationResponseDto> reservations;
}
