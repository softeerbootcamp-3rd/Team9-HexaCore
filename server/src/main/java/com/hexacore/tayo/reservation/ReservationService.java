package com.hexacore.tayo.reservation;

import com.hexacore.tayo.car.CarRepository;
import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.car.model.CarDateRange;
import com.hexacore.tayo.car.model.CarImage;
import com.hexacore.tayo.category.model.Subcategory;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.reservation.dto.CreateReservationRequestDto;
import com.hexacore.tayo.reservation.dto.GetCarSimpleResponseDto;
import com.hexacore.tayo.reservation.dto.GetGuestReservationListResponseDto;
import com.hexacore.tayo.reservation.dto.GetGuestReservationResponseDto;
import com.hexacore.tayo.reservation.dto.GetHostReservationListResponseDto;
import com.hexacore.tayo.reservation.dto.GetHostReservationResponseDto;
import com.hexacore.tayo.reservation.model.Reservation;
import com.hexacore.tayo.reservation.model.ReservationStatus;
import com.hexacore.tayo.user.UserRepository;
import com.hexacore.tayo.user.dto.GetUserSimpleResponseDto;
import com.hexacore.tayo.user.model.User;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createReservation(CreateReservationRequestDto createReservationRequestDto, Long guestUserId) {
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

    public GetGuestReservationListResponseDto getGuestReservations(Long guestUserId) {
        List<Reservation> reservations = reservationRepository.findAllByGuest_id(guestUserId);
        List<GetGuestReservationResponseDto> getGuestReservationResponseDtos = new ArrayList<>();
        for (Reservation reservation : reservations) {
            Car car = reservation.getCar();
            List<CarImage> images = car.getCarImages();
            Subcategory subcategory = car.getSubcategory();
            User host = car.getOwner();

            GetCarSimpleResponseDto getCarSimpleResponseDto = GetCarSimpleResponseDto.builder()
                    .id(car.getId())
                    .name(subcategory.getName())
                    .imageUrl(images.get(0).getUrl()) // 대표 이미지 1장
                    .build();

            GetGuestReservationResponseDto getGuestReservationResponseDto = GetGuestReservationResponseDto.builder()
                    .id(reservation.getId())
                    .car(getCarSimpleResponseDto)
                    .fee(reservation.getFee())
                    .carAddress(car.getAddress())
                    .rentDateTime(reservation.getRentDateTime())
                    .returnDateTime(reservation.getReturnDateTime())
                    .status(reservation.getStatus())
                    .hostPhoneNumber(host.getPhoneNumber())
                    .build();

            getGuestReservationResponseDtos.add(getGuestReservationResponseDto);
        }

        return new GetGuestReservationListResponseDto(getGuestReservationResponseDtos);
    }

    public GetHostReservationListResponseDto getHostReservations(Long hostUserId) {
        List<Reservation> reservations = reservationRepository.findAllByHost_id(hostUserId);
        List<GetHostReservationResponseDto> getHostReservationResponseDtos = new ArrayList<>();

        for (Reservation reservation : reservations) {
            User guest = reservation.getGuest();

            GetUserSimpleResponseDto userSimpleResponseDto = GetUserSimpleResponseDto.builder()
                    .id(guest.getId())
                    .name(guest.getName())
                    .phoneNumber(guest.getPhoneNumber())
                    .profileImgUrl(guest.getProfileImgUrl())
                    .build();

            GetHostReservationResponseDto getHostReservationResponseDto = GetHostReservationResponseDto.builder()
                    .id(reservation.getId())
                    .guest(userSimpleResponseDto)
                    .rentDateTime(reservation.getRentDateTime())
                    .returnDateTime(reservation.getReturnDateTime())
                    .fee(reservation.getFee())
                    .status(reservation.getStatus())
                    .build();

            getHostReservationResponseDtos.add(getHostReservationResponseDto);
        }

        return new GetHostReservationListResponseDto(getHostReservationResponseDtos);
    }

    @Transactional
    public void updateReservationStatus(Long userId, Long reservationId, String status) {
        ReservationStatus requestedStatus = ReservationStatus.getReservationStatus(status);

        if (requestedStatus == ReservationStatus.NOT_FOUND) {
            throw new GeneralException(ErrorCode.RESERVATION_STATUS_NOT_FOUND);
        }

        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new GeneralException(ErrorCode.RESERVATION_NOT_FOUND));

        ReservationStatus originStatus = reservation.getStatus();
        boolean invalidUpdate = false;

        // 호스트가 요청한 경우
        if (reservation.getHost().getId().equals(user.getId())) {
            // 예약 취소
            if (originStatus == ReservationStatus.READY && requestedStatus == ReservationStatus.CANCEL) {
                reservation.setStatus(requestedStatus);
            }
            // 반납 확인
            else if (originStatus == ReservationStatus.USING && requestedStatus == ReservationStatus.TERMINATED) {
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
}
