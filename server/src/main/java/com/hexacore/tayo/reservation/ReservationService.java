package com.hexacore.tayo.reservation;

import com.hexacore.tayo.car.carRepository.CarRepository;
import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.car.model.CarDateRange;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.lock.LockKeyGenerator;
import com.hexacore.tayo.lock.RangeLockManager;
import com.hexacore.tayo.notification.manager.NotificationManager;
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
import java.util.stream.Stream;
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
    private final RangeLockManager lockManager;
    private final PaymentManager paymentManager;

    @Transactional
    public CreateReservationResponseDto createReservation(Long guestId, CreateReservationRequestDto createReservationRequestDto) {
        LocalDateTime rentDateTime = createReservationRequestDto.getRentDateTime();
        LocalDateTime returnDateTime = createReservationRequestDto.getReturnDateTime();
        if (rentDateTime.isAfter(returnDateTime.minusHours(1))) {
            throw new GeneralException(ErrorCode.RESERVATION_DATETIME_LEAST_HOUR);
        }

        Long carId = createReservationRequestDto.getCarId();
        String lockKey = LockKeyGenerator.generateCarDateRangeLockKey(carId);
        if (!lockManager.acquireRangeLock(lockKey, rentDateTime.toLocalDate(), returnDateTime.toLocalDate())) {
            throw new GeneralException(ErrorCode.CAR_DATE_RANGE_LOCK_ACQUIRE_FAIL);
        }

        try {
            User guest = userRepository.findByIdAndIsDeletedFalse(guestId)
                    .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

            Car car = carRepository.findByIdAndIsDeletedFalse(carId)
                    .orElseThrow(() -> new GeneralException(ErrorCode.CAR_NOT_FOUND));

            if (guest.getId().equals(car.getOwner().getId())) {
                throw new GeneralException(ErrorCode.RESERVATION_HOST_EQUALS_GUEST);
            }

            List<CarDateRange> sortedCarDateRanges = car.getCarDateRanges();
            sortedCarDateRanges.sort(CarDateRange::compareTo);

            CarDateRange carDateRange = findCarDateRangeIncludesDateRange(sortedCarDateRanges, rentDateTime.toLocalDate(), returnDateTime.toLocalDate());
            if (carDateRange == null) {
                throw new GeneralException(ErrorCode.RESERVATION_DATE_NOT_IN_RANGE);
            }

            List<Reservation> reservations = reservationRepository.findAllByCar_idAndStatusInOrderByRentDateTimeAsc(
                    car.getId(),
                    List.of(ReservationStatus.READY, ReservationStatus.USING)
            );

            if (isOverlappedReservation(reservations, rentDateTime.toLocalDate(), returnDateTime.toLocalDate())) {
                throw new GeneralException(ErrorCode.RESERVATION_ALREADY_READY_OR_USING);
            }

            User host = car.getOwner();
            int fee = calculateTotalPrice(car.getFeePerHour(), rentDateTime, returnDateTime);

            Reservation reservation = Reservation.builder()
                    .guest(guest)
                    .host(host)
                    .car(car)
                    .fee(fee)
                    .rentDateTime(rentDateTime)
                    .returnDateTime(returnDateTime)
                    .status(ReservationStatus.READY)
                    .build();

            Reservation createdReservation = reservationRepository.save(reservation);

            return CreateReservationResponseDto.builder()
                    .reservationId(createdReservation.getId())
                    .fee(fee)
                    .hostId(host.getId())
                    .build();

        } finally {
            lockManager.releaseRangeLock(lockKey, rentDateTime.toLocalDate(), returnDateTime.toLocalDate());
        }
    }

    private CarDateRange findCarDateRangeIncludesDateRange(List<CarDateRange> sortedCarDateRanges, LocalDate rentDate, LocalDate returnDate) {
        for (CarDateRange carDateRange : sortedCarDateRanges) {
            if (rentDate.isBefore(carDateRange.getStartDate())) {
                return null;
            }
            if (returnDate.isAfter(carDateRange.getEndDate())) {
                continue;
            }
            return carDateRange;
        }
        return null;
    }

    private boolean isOverlappedReservation(List<Reservation> sortedReservations, LocalDate rentDate, LocalDate returnDate) {
        for (Reservation reservation : sortedReservations) {
            if (returnDate.isBefore(reservation.getRentDateTime().toLocalDate())) {
                return false;
            }
            if (rentDate.isAfter(reservation.getReturnDateTime().toLocalDate())) {
                continue;
            }
            return true;
        }
        return false;
    }

    private int calculateTotalPrice(Integer fee, LocalDateTime rentDateTime, LocalDateTime returnDateTime) {
        return fee * (int) rentDateTime.until(returnDateTime, ChronoUnit.HOURS);
    }

    @Transactional
    public Page<Reservation> getGuestReservations(Long guestUserId, Pageable pageable) {
        // Using, Ready, Cancel, Terminated 순으로 정렬하여 Page로 응답한다.
        Page<Reservation> reservations =
                reservationRepository.findAllByGuest_idOrderByStatusAscRentDateTimeAsc(guestUserId, pageable);
        updateReservationStatusByCurrentDateTime(reservations.stream());

        return reservations;
    }

    @Transactional
    public List<Reservation> getHostReservations(Long hostUserId) {
        // Using, Ready, Cancel, Terminated 순으로 정렬하여 List로 응답한다.
        List<Reservation> reservations =
                reservationRepository.findAllByHost_idOrderByStatusAscRentDateTimeAsc(hostUserId);
        updateReservationStatusByCurrentDateTime(reservations.stream());

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
            // 예약 취소
            if (originStatus == ReservationStatus.READY && requestedStatus == ReservationStatus.CANCEL) {
                if (currentDateTime.isAfter(rentDateTime.minusHours(24))) {
                    // 예약 시간 24시간 전부터 취소 금지
                    // currentDateTime > rentDateTime - 24Hour 이면 예외 처리
                    throw new GeneralException(ErrorCode.RESERVATION_CANCEL_TOO_LATE);
                }
                reservation.setStatus(requestedStatus);

                // 게스트가 예약을 취소하면 호스트에게 에약취소 알림을 전송
                notificationManager.notify(reservation.getHost().getId(), reservation.getGuest().getName(),
                        NotificationType.CANCEL);
            }

            // 반납 요청 및 추가 요금 과금
            else if (originStatus == ReservationStatus.USING && requestedStatus == ReservationStatus.TERMINATED) {
                reservation.setStatus(requestedStatus);
                Integer feePerHour = reservation.getCar().getFeePerHour();
                // 0~59분도 1시간 추가 요금으로 책정.
                // 연체할 경우 시간당 추가 과금 (returnDateTime <= currentDateTime)
                if (!returnDateTime.isAfter(currentDateTime)) {
                    int delayedHours = (int) returnDateTime.until(currentDateTime, ChronoUnit.HOURS);
                    reservation.setExtraFee(feePerHour * (delayedHours + 1));
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

    private void updateReservationStatusByCurrentDateTime(Stream<Reservation> reservations) {
        LocalDateTime currentDateTime = LocalDateTime.now();

        for (Reservation reservation : reservations.toList()) {
            boolean changed = false;
            ReservationStatus reservationStatus = reservation.getStatus();
            LocalDateTime rentDateTime = reservation.getRentDateTime();
            LocalDateTime returnDateTime = reservation.getReturnDateTime();

            if (reservationStatus == ReservationStatus.READY) {
                // Ready 상태일 때 rentDateTime <= currentDateTime 이면 Using 상태로 변경
                // if문은 !(rentDateTime > currentDateTime)
                if (!rentDateTime.isAfter(currentDateTime)) {
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
