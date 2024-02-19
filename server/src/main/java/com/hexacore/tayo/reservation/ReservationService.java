package com.hexacore.tayo.reservation;

import com.hexacore.tayo.car.CarRepository;
import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.car.model.CarDateRange;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.reservation.dto.CreateReservationRequestDto;
import com.hexacore.tayo.reservation.dto.TossPayment.TossApproveRequest;
import com.hexacore.tayo.reservation.dto.TossPayment.TossCancelRequest;
import com.hexacore.tayo.reservation.dto.TossPayment.TossPaymentResponse;
import com.hexacore.tayo.reservation.model.Reservation;
import com.hexacore.tayo.reservation.model.ReservationStatus;
import com.hexacore.tayo.user.UserRepository;
import com.hexacore.tayo.user.model.User;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Value("${toss.secret-key}")
    private String tossSecretKey;

    @Transactional
    public void createReservation(CreateReservationRequestDto createReservationRequestDto,
            Long guestUserId,
            Integer amount) throws Exception {
        User guestUser = userRepository.findByIdAndIsDeletedFalse(guestUserId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        Car car = carRepository.findByIdAndIsDeletedFalse(createReservationRequestDto.getCarId())
                .orElseThrow(() -> new GeneralException(ErrorCode.CAR_NOT_FOUND));

        if (guestUser.getId().equals(car.getOwner().getId())) {
            throw new GeneralException(ErrorCode.RESERVATION_HOST_EQUALS_GUEST);
        }

        User hostUser = car.getOwner();

        LocalDateTime rentDateTime = createReservationRequestDto.getRentDateTime();
        LocalDateTime returnDateTime = createReservationRequestDto.getReturnDateTime();

        // rentDateTime, returnDateTime이 범위안에 있는지 검증
        // 아니라면 RESERVATION_DATE_NOT_IN_RANGE 예외 발생
        // 범위 안이라면 이미 있는 예약과 겹치는 지 검증
        // 겹치면 RESERVATION_ALREADY_READY_OR_USING 예외 발생
        validateRentReturnInRangeElseThrow(car, rentDateTime, returnDateTime);

        // 유저가 결제한 금액과 예약 fee 가 일치하는 지 확인
        Integer fee = car.getFeePerHour() * (int) rentDateTime.until(returnDateTime, ChronoUnit.HOURS);
        if (!fee.equals(amount)) {
            throw new GeneralException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }

        Reservation reservation = Reservation.builder()
                .guest(guestUser)
                .host(hostUser)
                .car(car)
                .fee(car.getFeePerHour() * (int) rentDateTime.until(returnDateTime, ChronoUnit.HOURS))
                .rentDateTime(createReservationRequestDto.getRentDateTime())
                .returnDateTime(createReservationRequestDto.getReturnDateTime())
                .status(ReservationStatus.READY)
                .build();
        reservationRepository.save(reservation);
    }

    @Transactional
    public Page<Reservation> getGuestReservations(Long guestUserId, Pageable pageable) {
        Page<Reservation> reservations = reservationRepository.findAllByGuest_id(guestUserId, pageable);
        updateReservationStatusByCurrentDateTime(reservations);

        return reservations;
    }

    @Transactional
    public Page<Reservation> getHostReservations(Long hostUserId, Pageable pageable) {
        Page<Reservation> reservations = reservationRepository.findAllByHost_id(hostUserId, pageable);
        updateReservationStatusByCurrentDateTime(reservations);

        return reservations;
    }

    @Transactional
    public void updateReservationStatus(Long userId, Long reservationId, String status) {
        ReservationStatus requestedStatus = ReservationStatus.getReservationStatus(status);

        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new GeneralException(ErrorCode.RESERVATION_NOT_FOUND));

        ReservationStatus originStatus = reservation.getStatus();
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime rentDateTime = reservation.getRentDateTime();
        LocalDateTime returnDateTime = reservation.getReturnDateTime();
        boolean invalidUpdate = false;

        // 호스트가 요청한 경우
        if (reservation.getHost().getId().equals(user.getId())) {
            // 예약 취소
            if (originStatus == ReservationStatus.READY && requestedStatus == ReservationStatus.CANCEL) {
                reservation.setStatus(requestedStatus);
            } else {
                invalidUpdate = true;
            }
        }
        // 게스트가 요청한 경우
        else if (reservation.getGuest().getId().equals(user.getId())) {
            // 예약 취소 혹은 대여 시작
            if (originStatus == ReservationStatus.READY &&
                    (requestedStatus == ReservationStatus.CANCEL || requestedStatus == ReservationStatus.USING)) {
                reservation.setStatus(requestedStatus);
            }
            // 반납 요청 및 추가 요금 과금
            else if (originStatus == ReservationStatus.USING && requestedStatus == ReservationStatus.TERMINATED) {
                reservation.setStatus(requestedStatus);
                Integer feePerHour = reservation.getCar().getFeePerHour();
                // 연체할 경우 시간당 추가 과금 (returnDateTime <= currentDateTime)
                if (!returnDateTime.isAfter(currentDateTime)) {
                    reservation.setExtraFee(feePerHour * (int) returnDateTime.until(currentDateTime, ChronoUnit.HOURS));
                }
            } else {
                invalidUpdate = true;
            }
        } else {
            throw new GeneralException(ErrorCode.RESERVATION_STATUS_CHANGED_BY_OTHERS);
        }

        if (invalidUpdate) {
            throw new GeneralException(ErrorCode.RESERVATION_STATUS_INVALID_CHANGE);
        }

        reservation.setStatus(requestedStatus);
        reservationRepository.save(reservation);
    }

    private void validateRentReturnInRangeElseThrow(Car car,
            LocalDateTime rentDateTime,
            LocalDateTime returnDateTime) throws GeneralException {
        if (rentDateTime.isAfter(returnDateTime)) {
            throw new GeneralException(ErrorCode.START_DATE_AFTER_END_DATE);
        }

        // 최소 1시간이어야 한다.
        // rentDateTime + 1Hour <= returnDateTime
        // rentDateTime + 1Hour > returnDateTime 일때 에러
        if (rentDateTime.plusHours(1).isAfter(returnDateTime)) {
            throw new GeneralException(ErrorCode.RESERVATION_DATETIME_LEAST_HOUR);
        }

        List<Reservation> reservations = reservationRepository.findAllByCar_idAndStatusInOrderByRentDateTimeAsc(
                car.getId(),
                List.of(ReservationStatus.READY, ReservationStatus.USING)
        );

        for (CarDateRange carDateRange : car.getCarDateRanges()) {
            LocalDate startDate = carDateRange.getStartDate();
            LocalDate endDate = carDateRange.getEndDate();

            // 포함되는 예약 가능 구간을 찾을 때 까지는 continue;
            if (startDate.isAfter(rentDateTime.toLocalDate()) || endDate.isBefore(rentDateTime.toLocalDate())) {
                continue;
            }

            for (Reservation reservation : reservations) {
                // 기존의 예약과 겹치는지
                if (!(returnDateTime.isBefore(reservation.getRentDateTime())
                        || rentDateTime.isAfter(reservation.getReturnDateTime()))) {
                    throw new GeneralException(ErrorCode.RESERVATION_ALREADY_READY_OR_USING);
                }
            }
            return;
        }
        throw new GeneralException(ErrorCode.RESERVATION_DATE_NOT_IN_RANGE);
    }

    private void updateReservationStatusByCurrentDateTime(Page<Reservation> reservations) {
        LocalDateTime currentDateTime = LocalDateTime.now();

        for (Reservation reservation : reservations) {
            boolean changed = false;
            ReservationStatus reservationStatus = reservation.getStatus();
            LocalDateTime rentDateTime = reservation.getRentDateTime();
            LocalDateTime returnDateTime = reservation.getReturnDateTime();

            if (reservationStatus == ReservationStatus.READY) {
                // Ready 상태일 때 rentDate == currentDate 이면 (동일 날짜라면) Using 상태로 변경
                if (rentDateTime.toLocalDate().isEqual(currentDateTime.toLocalDate())) {
                    reservation.setStatus(ReservationStatus.USING);
                    changed = true;
                }
            }

            if (changed) {
                reservationRepository.save(reservation);
            }
        }
    }

    public void confirmPayments(String paymentKey, String orderId, Integer amount) {
        String encodedCredentials = getEncodedCredentials();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + encodedCredentials);
        HttpEntity<TossApproveRequest> requestEntity = new HttpEntity<>(
                TossApproveRequest.builder().paymentKey(paymentKey).orderId(orderId).amount(amount).build(), headers);

        try {
            ResponseEntity<TossPaymentResponse> response = restTemplate.exchange(
                    "https://api.tosspayments.com/v1/payments/confirm",
                    HttpMethod.POST,
                    requestEntity,
                    TossPaymentResponse.class
            );

            if (response.getBody() == null || !"DONE".equals(response.getBody().getStatus())) {
                // 결제 승인 실패
                throw new GeneralException(ErrorCode.TOSS_PAYMENTS_FAILED);
            }
        } catch (Exception e) {
            throw new GeneralException(ErrorCode.TOSS_PAYMENTS_FAILED, e.getMessage());
        }
    }

    public void cancelPayments(String paymentKey, String cancelReason) {
        String encodedCredentials = getEncodedCredentials();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + encodedCredentials);
        HttpEntity<TossCancelRequest> requestEntity = new HttpEntity<>(
                TossCancelRequest.builder().cancelReason(cancelReason).build(), headers);
        try {
            ResponseEntity<TossPaymentResponse> response = restTemplate.exchange(
                    String.format("https://api.tosspayments.com/v1/payments/%s/cancel", paymentKey),
                    HttpMethod.POST,
                    requestEntity,
                    TossPaymentResponse.class
            );
            if (!response.getBody().getStatus().equals("CANCELED")) {
                throw new GeneralException(ErrorCode.TOSS_PAYMENTS_CANCEL_FAILED);
            }
        } catch (Exception e) {
            throw new GeneralException(ErrorCode.TOSS_PAYMENTS_CANCEL_FAILED, e.getMessage());
        }
    }

    public String getEncodedCredentials() {
        String credentials = tossSecretKey + ":";
        return new String(Base64.getEncoder().encode(credentials.getBytes()));
    }
}
