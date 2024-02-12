package com.hexacore.tayo.reservation.model;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class GuestReservationDto {

    private final Long id;

    private final HostCarDto car;

    private final Integer fee;
    private final String carAddress;
    private final Date rentDate;
    private final Date returnDate;
    private final ReservationStatus status;
    private final String hostPhoneNumber;
}
