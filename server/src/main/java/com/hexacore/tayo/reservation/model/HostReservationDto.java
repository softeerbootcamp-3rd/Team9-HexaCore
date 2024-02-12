package com.hexacore.tayo.reservation.model;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class HostReservationDto {

    private final Long id;

    private final GuestDto guest;

    private final Date rentDate;
    private final Date returnDate;
    private final Integer fee;
    private final ReservationStatus status;
}
