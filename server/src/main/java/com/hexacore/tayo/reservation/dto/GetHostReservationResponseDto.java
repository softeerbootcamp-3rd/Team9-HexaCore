package com.hexacore.tayo.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hexacore.tayo.reservation.model.Reservation;
import com.hexacore.tayo.reservation.model.ReservationStatus;
import com.hexacore.tayo.user.dto.GetUserSimpleResponseDto;
import com.hexacore.tayo.user.model.User;
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
    private final Integer extraFee;
    private final ReservationStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime rentDateTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime returnDateTime;

    public static GetHostReservationResponseDto of(Reservation reservation) {
        User guest = reservation.getGuest();
        GetUserSimpleResponseDto userSimpleResponseDto = GetUserSimpleResponseDto.builder()
                .id(guest.getId())
                .name(guest.getName())
                .phoneNumber(guest.getPhoneNumber())
                .profileImgUrl(guest.getProfileImgUrl())
                .build();

        return GetHostReservationResponseDto.builder()
                .id(reservation.getId())
                .guest(userSimpleResponseDto)
                .fee(reservation.getFee())
                .extraFee(reservation.getExtraFee())
                .status(reservation.getStatus())
                .rentDateTime(reservation.getRentDateTime())
                .returnDateTime(reservation.getReturnDateTime())
                .build();
    }

    @Override
    public String toString() {
        return "{"
                + "id=" + id
                + ", guest=" + guest
                + ", fee=" + fee
                + ", extraFee=" + extraFee
                + ", status=" + status
                + ", rentDateTime=" + rentDateTime
                + ", returnDateTime=" + returnDateTime
                + '}';
    }
}
