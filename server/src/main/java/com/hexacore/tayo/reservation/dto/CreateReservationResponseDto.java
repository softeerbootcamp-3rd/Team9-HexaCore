package com.hexacore.tayo.reservation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateReservationResponseDto {

    private Long reservationId;
    private Integer fee;
    private Long hostId;
}
