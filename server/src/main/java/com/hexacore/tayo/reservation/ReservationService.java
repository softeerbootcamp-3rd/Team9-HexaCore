package com.hexacore.tayo.reservation;

import com.hexacore.tayo.car.CarRepository;
import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.car.model.CarDateRange;
import com.hexacore.tayo.car.model.CarImage;
import com.hexacore.tayo.category.model.SubCategory;
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
        User guestUser = userRepository.findById(guestUserId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        Car car = carRepository.findById(createReservationRequestDto.getCarId())
                .orElseThrow(() -> new GeneralException(ErrorCode.CAR_NOT_FOUND));

        if (guestUser.getId() == car.getOwner().getId()) {
            throw new GeneralException(ErrorCode.RESERVATION_HOST_EQUALS_GUEST);
        }

        User hostUser = car.getOwner();
        List<CarDateRange> carDateRanges = car.getCarDateRanges();

        LocalDateTime rentDateTime = createReservationRequestDto.getRentDateTime();
        LocalDateTime returnDateTime = createReservationRequestDto.getReturnDateTime();

        // rentDateTime, returnDateTime이 범위안에 있는지 검증
        // 없으면 GeneralException.RESERVATION_DATE_NOT_IN_RANGE 예외 발생
        CarDateRange validCarDateRange = getCarDateInRangeElseThrow(carDateRanges, rentDateTime, returnDateTime);

        Reservation reservation = Reservation.builder()
                .guest(guestUser)
                .host(hostUser)
                .fee(car.getFeePerHour() * (int) rentDateTime.until(returnDateTime, ChronoUnit.HOURS))
                .carDateRange(validCarDateRange)
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
            Car car = reservation.getCarDateRange().getCar();
            List<CarImage> images = car.getCarImages();
            SubCategory subCategory = car.getSubCategory();
            User host = car.getOwner();

            GetCarSimpleResponseDto getCarSimpleResponseDto = GetCarSimpleResponseDto.builder()
                    .id(car.getId())
                    .name(subCategory.getName())
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
    public void cancelReservation(Long hostUserId, Long reservationId) {
        User hostUser = userRepository.findById(hostUserId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new GeneralException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getHost().equals(hostUser)) {
            throw new GeneralException(ErrorCode.RESERVATION_CANCELED_BY_OTHERS);
        }

        reservation.setStatus(ReservationStatus.CANCEL);
        reservationRepository.save(reservation);
    }

    private CarDateRange getCarDateInRangeElseThrow(List<CarDateRange> carDateRanges,
            LocalDateTime rentDateTime,
            LocalDateTime returnDateTime) throws GeneralException {

        if (carDateRanges.isEmpty()) {
            throw new GeneralException(ErrorCode.RESERVATION_DATE_NOT_IN_RANGE);
        }

        for (CarDateRange carDateRange : carDateRanges) {
            LocalDate startDate = carDateRange.getStartDate();
            LocalDate endDate = carDateRange.getEndDate();

            if (localDateInclusiveBefore(startDate, rentDateTime.toLocalDate())) {
                if (localDateInclusiveAfter(endDate, returnDateTime.toLocalDate())) {
                    return carDateRange;
                }
                continue;
            }
            break;
        }
        throw new GeneralException(ErrorCode.RESERVATION_DATE_NOT_IN_RANGE);
    }

    private boolean localDateInclusiveBefore(LocalDate startDate, LocalDate rentDate) {
        return startDate.isEqual(rentDate) || startDate.isBefore(rentDate);
    }

    private boolean localDateInclusiveAfter(LocalDate endDate, LocalDate returnDate) {
        return endDate.isEqual(returnDate) || endDate.isAfter(returnDate);
    }
}
