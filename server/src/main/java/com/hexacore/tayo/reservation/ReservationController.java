package com.hexacore.tayo.reservation;

import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.common.response.Response;
import com.hexacore.tayo.reservation.dto.CreateReservationRequestDto;
import com.hexacore.tayo.reservation.dto.GetGuestReservationListResponseDto;
import com.hexacore.tayo.reservation.dto.GetHostReservationListResponseDto;
import com.hexacore.tayo.reservation.dto.UpdateReservationStatusRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<Response> createReservation(HttpServletRequest request,
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Integer amount,
            @Valid @RequestBody CreateReservationRequestDto createReservationRequestDto) {
        Long guestUserId = (Long) request.getAttribute("userId");
        // 결제 승인 요청
        reservationService.confirmPayments(paymentKey, orderId, amount);
        // 결제가 성공하면 createReservation 실행
        try {
            reservationService.createReservation(createReservationRequestDto, guestUserId, amount);
        } catch (Exception e) {
            // 예약 내역 저장에 실패하면 결제 취소
            reservationService.cancelPayments(paymentKey, "예약에 실패했습니다.");
            throw new GeneralException(e);
        }

        return Response.of(HttpStatus.CREATED);
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

    @PatchMapping("/{reservationId}")
    public ResponseEntity<Response> updateReservationStatus(HttpServletRequest request,
            @PathVariable Long reservationId,
            @RequestBody UpdateReservationStatusRequestDto statusDto) {
        Long userId = (Long) request.getAttribute("userId");
        String status = statusDto.getStatus();

        reservationService.updateReservationStatus(userId, reservationId, status);
        return Response.of(HttpStatus.NO_CONTENT);
    }
}
