package com.hexacore.tayo.reservation.dto;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@RequiredArgsConstructor
@Builder
public class CreateReservationRequestDto {

    private final Long carId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final Date rentDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final Date returnDate;
}
