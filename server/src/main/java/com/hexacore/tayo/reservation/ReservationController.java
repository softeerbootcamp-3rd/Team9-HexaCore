package com.hexacore.tayo.reservation;

import com.hexacore.tayo.common.response.Response;
import com.hexacore.tayo.reservation.dto.CreateReservationRequestDto;
import com.hexacore.tayo.reservation.dto.GetGuestReservationListResponseDto;
import com.hexacore.tayo.reservation.dto.GetHostReservationListResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping()
    public ResponseEntity<Response> createReservation(
            @ModelAttribute CreateReservationRequestDto createReservationRequestDto) {

        reservationService.createReservation(createReservationRequestDto);
        return Response.of(HttpStatus.OK);
    }

    @GetMapping("/guest")
    public ResponseEntity<Response> guestReservations() {
        GetGuestReservationListResponseDto getGuestReservationListResponseDto = reservationService.getGuestReservations();
        return Response.of(HttpStatus.OK, getGuestReservationListResponseDto);
    }

    @GetMapping("/host")
    public ResponseEntity<Response> hostReservations() {
        GetHostReservationListResponseDto getHostReservationListResponseDto = reservationService.getHostReservations();
        return Response.of(HttpStatus.OK, getHostReservationListResponseDto);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Response> cancelReservation(@PathVariable Long reservationId) {
        reservationService.cancelReservation(reservationId);
        return Response.of(HttpStatus.OK);
    }
}
