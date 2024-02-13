package com.hexacore.tayo.reservation;

import com.hexacore.tayo.common.ResponseDto;
import com.hexacore.tayo.reservation.dto.CreateReservationDto;
import com.hexacore.tayo.reservation.dto.CreateReservationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping()
    public ResponseEntity<ResponseDto> createReservation(
            @ModelAttribute CreateReservationRequestDto createReservationRequestDto) {
        CreateReservationDto createReservationDto = CreateReservationDto.builder()
                .carId(createReservationRequestDto.getCarId())
                .rentDate(createReservationRequestDto.getRentDate())
                .returnDate(createReservationRequestDto.getReturnDate())
                .build();

        ResponseDto responseDto = reservationService.createReservation(createReservationDto);
        return ResponseEntity
                .status(responseDto.getCode())
                .body(responseDto);
    }

    @GetMapping("/guest")
    public ResponseEntity<ResponseDto> guestReservations() {
        ResponseDto responseDto = reservationService.getGuestReservations();
        return ResponseEntity
                .status(responseDto.getCode())
                .body(responseDto);
    }

    @GetMapping("/host")
    public ResponseEntity<ResponseDto> hostReservations() {
        ResponseDto responseDto = reservationService.getHostReservations();
        return ResponseEntity
                .status(responseDto.getCode())
                .body(responseDto);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ResponseDto> cancelReservation(@PathVariable Long reservationId) {
        ResponseDto responseDto = reservationService.cancelReservation(reservationId);
        return ResponseEntity
                .status(responseDto.getCode())
                .body(responseDto);
    }
}
