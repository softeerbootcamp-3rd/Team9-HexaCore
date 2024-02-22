package com.hexacore.tayo.reservation;

import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.common.response.Response;
import com.hexacore.tayo.notification.manager.NotificationManager;
import com.hexacore.tayo.notification.model.NotificationType;
import com.hexacore.tayo.reservation.dto.CreateReservationRequestDto;
import com.hexacore.tayo.reservation.dto.CreateReservationResponseDto;
import com.hexacore.tayo.reservation.dto.GetGuestReservationResponseDto;
import com.hexacore.tayo.reservation.dto.GetHostReservationResponseDto;
import com.hexacore.tayo.reservation.dto.UpdateReservationStatusRequestDto;
import com.hexacore.tayo.reservation.model.Reservation;
import com.hexacore.tayo.review.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final ReviewService reviewService;
    private final NotificationManager notificationManager;

    @PostMapping
    public ResponseEntity<Response> createReservation(HttpServletRequest request,
            @Valid @RequestParam String orderName,
            @Valid @RequestParam String userName,
            @Valid @RequestBody CreateReservationRequestDto createReservationRequestDto) {
        Long guestUserId = (Long) request.getAttribute("userId");

        // 예약 진행: DB 업데이트
        CreateReservationResponseDto createReservationResponseDto = reservationService.createReservation(
                createReservationRequestDto, guestUserId);

        // 자동 결제 승인 요청
        try {
            reservationService.confirmBilling(guestUserId, createReservationResponseDto.getReservationId(),
                    createReservationResponseDto.getFee(), orderName, userName);
        } catch (Exception e) {
            // 결제 실패 시 DB에 저장된 예약 정보 삭제
            reservationService.rollBackReservation(createReservationResponseDto.getReservationId());
            throw new GeneralException(e.getMessage());
        }

        // 예약이 완료되면 호스트에게 에약완료 알림을 전송
        notificationManager.notify(createReservationResponseDto.getHostId(), userName, NotificationType.RESERVE);

        return Response.of(HttpStatus.CREATED);
    }

    @GetMapping("/guest")
    public ResponseEntity<Response> guestReservations(HttpServletRequest request, Pageable pageable) {
        Long guestUserId = (Long) request.getAttribute("userId");

        Page<Reservation> reservations = reservationService.getGuestReservations(guestUserId, pageable);
        Page<GetGuestReservationResponseDto> data = reservations.map(
                (reservation -> GetGuestReservationResponseDto.of(reservation,
                        reviewService.isReviewed(reservation, true))));
        return Response.of(HttpStatus.OK, data);
    }

    @GetMapping("/host")
    public ResponseEntity<Response> hostReservations(HttpServletRequest request) {
        Long hostUserId = (Long) request.getAttribute("userId");

        List<Reservation> reservations = reservationService.getHostReservations(hostUserId);
        List<GetHostReservationResponseDto> data = reservations
                .stream()
                .map(reservation -> GetHostReservationResponseDto.of(reservation,
                        reviewService.isReviewed(reservation, false)))
                .toList();
        return Response.of(HttpStatus.OK, data);
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
