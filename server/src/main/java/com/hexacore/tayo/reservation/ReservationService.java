package com.hexacore.tayo.reservation;

import com.hexacore.tayo.car.CarRepository;
import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.car.model.CarDateRange;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.notification.NotificationManager;
import com.hexacore.tayo.notification.model.NotificationType;
import com.hexacore.tayo.reservation.dto.CreateReservationRequestDto;
import com.hexacore.tayo.reservation.dto.CreateReservationResponseDto;
import com.hexacore.tayo.reservation.model.Reservation;
import com.hexacore.tayo.reservation.model.ReservationStatus;
import com.hexacore.tayo.user.UserRepository;
import com.hexacore.tayo.user.model.User;
import com.hexacore.tayo.util.payment.PaymentManager;
import com.hexacore.tayo.util.payment.TossPaymentDto.TossPaymentResponse;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final NotificationManager notificationManager;

    private final PaymentManager paymentManager;

    @Transactional
    public CreateReservationResponseDto createReservation(CreateReservationRequestDto createReservationRequestDto,
            Long guestUserId) {
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

        Integer fee = car.getFeePerHour() * (int) rentDateTime.until(returnDateTime, ChronoUnit.HOURS);

        Reservation reservation = Reservation.builder()
                .guest(guestUser)
                .host(hostUser)
                .car(car)
                .fee(fee)
                .rentDateTime(createReservationRequestDto.getRentDateTime())
                .returnDateTime(createReservationRequestDto.getReturnDateTime())
                .status(ReservationStatus.READY)
                .build();

        Reservation createdReservation = reservationRepository.save(reservation);
        return CreateReservationResponseDto.builder().reservationId(createdReservation.getId()).fee(fee)
                .hostId(hostUser.getId()).build();
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

                // 호스트가 예약을 거절하면 게스트에게 에약거절 알림을 전송
                notificationManager.notify(reservation.getHost().getId(), reservation.getGuest().getName(),
                        NotificationType.REFUSE);
            } else {
                invalidUpdate = true;
            }
        }

        // 게스트가 요청한 경우
        else if (reservation.getGuest().getId().equals(user.getId())) {
            // 예약 취소 혹은 대여 시작
            if (originStatus == ReservationStatus.READY && requestedStatus == ReservationStatus.CANCEL) {
                reservation.setStatus(requestedStatus);

                // 게스트가 예약을 취소하면 호스트에게 에약취소 알림을 전송
                notificationManager.notify(reservation.getHost().getId(), reservation.getGuest().getName(),
                        NotificationType.CANCEL);
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

    /* 카드 자동결제 승인 요청 */
    @Transactional
    public void confirmBilling(Long userId, Long reservationId, Integer amount, String orderName, String customerName) {
        // user의 billingKey 가져오기
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        if (user.getBillingKey() == null) {
            throw new GeneralException(ErrorCode.USER_BILLING_KEY_NOT_EXIST);
        }

        // 결제 요청
        TossPaymentResponse response = paymentManager.confirmBilling(amount, orderName, customerName,
                user.getCustomerKey(), user.getBillingKey());

        // 예약 테이블의 paymentKey 업데이트
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new GeneralException(ErrorCode.RESERVATION_NOT_FOUND));
        reservation.setPaymentKey(response.getPaymentKey());
        reservationRepository.save(reservation);
    }

    /* 결제 실패 시 예약 정보 삭제 */
    public void rollBackReservation(Long reservationId) {
        reservationRepository.deleteById(reservationId);
    }
}
