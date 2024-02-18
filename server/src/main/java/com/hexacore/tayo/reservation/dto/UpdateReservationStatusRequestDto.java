package com.hexacore.tayo.reservation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateReservationStatusRequestDto {

    private String status;
}
