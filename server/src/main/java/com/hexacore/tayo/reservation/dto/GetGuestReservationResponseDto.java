package com.hexacore.tayo.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hexacore.tayo.reservation.model.ReservationStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class GetGuestReservationResponseDto {

    private final Long id;
    private final GetCarSimpleResponseDto car;
    private final Integer fee;
    private final String carAddress;
    private final ReservationStatus status;
    private final String hostPhoneNumber;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime rentDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime returnDate;
}