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
import java.time.LocalDateTime;
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

        User hostUser = car.getOwner();
        List<CarDateRange> carDateRanges = car.getCarDateRanges();

        LocalDateTime rentDate = createReservationRequestDto.getRentDate();
        LocalDateTime returnDate = createReservationRequestDto.getReturnDate();

        // rentDate, returnDate가 범위안에 있는지 검증
        // 없으면 GeneralException.RESERVATION_DATE_NOT_IN_RANGE 예외 발생
        CarDateRange validCarDateRange = getCarDateInRangeElseThrow(carDateRanges, rentDate, returnDate);

        Reservation reservation = Reservation.builder()
                .guest(guestUser)
                .host(hostUser)
                .carDateRange(validCarDateRange)
                .rentDate(createReservationRequestDto.getRentDate())
                .returnDate(createReservationRequestDto.getReturnDate())
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
                    .fee(car.getFeePerHour())
                    .carAddress(car.getAddress())
                    .rentDate(reservation.getRentDate())
                    .returnDate(reservation.getReturnDate())
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
            Car car = reservation.getCarDateRange().getCar();
            User guest = reservation.getGuest();

            GetUserSimpleResponseDto userSimpleResponseDto = GetUserSimpleResponseDto.builder()
                    .id(guest.getId())
                    .phoneNumber(guest.getPhoneNumber())
                    .profileImgUrl(guest.getProfileImgUrl())
                    .build();

            GetHostReservationResponseDto getHostReservationResponseDto = GetHostReservationResponseDto.builder()
                    .id(reservation.getId())
                    .guest(userSimpleResponseDto)
                    .rentDate(reservation.getRentDate())
                    .returnDate(reservation.getReturnDate())
                    .fee(car.getFeePerHour())
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
            LocalDateTime rentDate,
            LocalDateTime returnDate) throws GeneralException {

        if (carDateRanges.isEmpty()) {
            throw new GeneralException(ErrorCode.RESERVATION_DATE_NOT_IN_RANGE);
        }

        for (CarDateRange carDateRange : carDateRanges) {
            LocalDateTime startDate = carDateRange.getStartDate();
            LocalDateTime endDate = carDateRange.getEndDate();

            if (startDate.isBefore(rentDate)) {
                if (endDate.isAfter(returnDate)) {
                    return carDateRange;
                }
                continue;
            }
            break;
        }
        throw new GeneralException(ErrorCode.RESERVATION_DATE_NOT_IN_RANGE);
    }
}
