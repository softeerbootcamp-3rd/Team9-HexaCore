package com.hexacore.tayo.reservation.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private final Date rentDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private final Date returnDate;
    private final Integer fee;
    private final ReservationStatus status;
}
