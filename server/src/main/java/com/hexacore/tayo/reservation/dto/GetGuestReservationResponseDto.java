package com.hexacore.tayo.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.reservation.model.Reservation;
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
    private final Integer extraFee;
    private final String carAddress;
    private final ReservationStatus status;
    private final String hostPhoneNumber;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime rentDateTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime returnDateTime;

    public static GetGuestReservationResponseDto of(Reservation reservation) {
        Car car = reservation.getCar();
        GetCarSimpleResponseDto carSimpleResponseDto = GetCarSimpleResponseDto.builder()
                .id(car.getId())
                .name(car.getSubcategory().getName())
                .imageUrl(car.getCarImages().get(0).getUrl()) // 대표 이미지 1장
                .lat(car.getPosition().getY())
                .lng(car.getPosition().getX())
                .build();

        return GetGuestReservationResponseDto.builder()
                .id(reservation.getId())
                .car(carSimpleResponseDto)
                .fee(reservation.getFee())
                .extraFee(reservation.getExtraFee())
                .carAddress(car.getAddress())
                .status(reservation.getStatus())
                .hostPhoneNumber(reservation.getHost().getPhoneNumber())
                .rentDateTime(reservation.getRentDateTime())
                .returnDateTime(reservation.getReturnDateTime())
                .build();
    }

    @Override
    public String toString() {
        return "{"
                + "id=" + id
                + ", car=" + car
                + ", fee=" + fee
                + ", extraFee=" + extraFee
                + ", carAddress='" + carAddress + '\''
                + ", status=" + status
                + ", hostPhoneNumber='" + hostPhoneNumber + '\''
                + ", rentDateTime=" + rentDateTime
                + ", returnDateTime=" + returnDateTime
                + '}';
    }
}
