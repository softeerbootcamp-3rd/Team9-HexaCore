package com.hexacore.tayo.reservation;

import com.hexacore.tayo.common.response.Response;
import com.hexacore.tayo.reservation.dto.CreateReservationRequestDto;
import com.hexacore.tayo.reservation.dto.GetGuestReservationListResponseDto;
import com.hexacore.tayo.reservation.dto.GetHostReservationListResponseDto;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<Response> createReservation(HttpServletRequest request,
            @ModelAttribute CreateReservationRequestDto createReservationRequestDto) {
        Long guestUserId = (Long) request.getAttribute("userId");

        reservationService.createReservation(createReservationRequestDto, guestUserId);
        return Response.of(HttpStatus.OK);
    }

    @GetMapping("/guest")
    public ResponseEntity<Response> guestReservations(HttpServletRequest request) {
        Long guestUserId = (Long) request.getAttribute("userId");

        GetGuestReservationListResponseDto getGuestReservationListResponseDto =
                reservationService.getGuestReservations(guestUserId);
        return Response.of(HttpStatus.OK, getGuestReservationListResponseDto);
    }

    @GetMapping("/host")
    public ResponseEntity<Response> hostReservations(HttpServletRequest request) {
        Long hostUserId = (Long) request.getAttribute("userId");

        GetHostReservationListResponseDto getHostReservationListResponseDto
                = reservationService.getHostReservations(hostUserId);
        return Response.of(HttpStatus.OK, getHostReservationListResponseDto);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Response> cancelReservation(HttpServletRequest request, @PathVariable Long reservationId) {
        Long hostUserId = (Long) request.getAttribute("userId");

        reservationService.cancelReservation(hostUserId, reservationId);
        return Response.of(HttpStatus.OK);
    }
}
