package com.hexacore.tayo.reservation.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private final ReservationStatus status;
    private final String hostPhoneNumber;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private final Date rentDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private final Date returnDate;
}
