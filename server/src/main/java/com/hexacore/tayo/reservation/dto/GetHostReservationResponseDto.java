package com.hexacore.tayo.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hexacore.tayo.reservation.model.ReservationStatus;
import com.hexacore.tayo.user.dto.GetUserSimpleResponseDto;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class GetHostReservationResponseDto {

    private final Long id;
    private final GetUserSimpleResponseDto guest;
    private final Integer fee;
    private final ReservationStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime rentDateTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime returnDateTime;

    @Override
    public String toString() {
        return "{"
                + "id=" + id
                + ", guest=" + guest
                + ", fee=" + fee
                + ", status=" + status
                + ", rentDateTime=" + rentDateTime
                + ", returnDateTime=" + returnDateTime
                + '}';
    }
}
