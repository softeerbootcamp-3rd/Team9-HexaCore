package com.hexacore.tayo.reservation;

import com.hexacore.tayo.car.CarImageRepository;
import com.hexacore.tayo.car.CarRepository;
import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.car.model.CarImage;
import com.hexacore.tayo.category.model.SubCategory;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.common.response.DataResponseDto;
import com.hexacore.tayo.common.response.ResponseDto;
import com.hexacore.tayo.reservation.dto.CreateReservationRequestDto;
import com.hexacore.tayo.reservation.dto.GetGuestReservationListResponseDto;
import com.hexacore.tayo.reservation.dto.GetHostReservationListResponseDto;
import com.hexacore.tayo.reservation.dto.GetGuestReservationResponseDto;
import com.hexacore.tayo.reservation.dto.GetCarSimpleResponseDto;
import com.hexacore.tayo.reservation.dto.GetHostReservationResponseDto;
import com.hexacore.tayo.reservation.model.Reservation;
import com.hexacore.tayo.reservation.model.ReservationStatus;
import com.hexacore.tayo.user.dto.GetUserSimpleResponseDto;
import com.hexacore.tayo.user.model.User;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;
    private final CarImageRepository imageRepository;

    @Transactional
    public ResponseDto createReservation(CreateReservationRequestDto createReservationRequestDto) {
        long guestUserId = 13L; // TODO: JWT 토큰에서 userId를 추출하고 로그인한 상태에서만 실행되도록 추가
        // 현재 예시는 Guest 유저의 ID:13

        // TODO: userRepository에서 userEntity 제공받기
        User guestUser = User.builder().id(guestUserId).build();

        Car car = carRepository.findById(createReservationRequestDto.getCarId())
                .orElseThrow(() -> new GeneralException(ErrorCode.CAR_NOT_FOUND));

        User hostUser = car.getOwner();

        List<List<LocalDateTime>> possibleRentDates = car.getDates();

        LocalDateTime rentDate = createReservationRequestDto.getRentDate();
        LocalDateTime returnDate = createReservationRequestDto.getReturnDate();

        possibleRentDates.stream()
                // date.get(0) ~ date.get(1) 사이의 날짜인지 검증
                .filter(date -> date.get(0).isBefore(rentDate))
                .filter(date -> date.get(1).isAfter(returnDate))
                .findFirst()
                .orElseThrow(() -> new GeneralException(ErrorCode.RESERVATION_DATE_NOT_IN_RANGE));

        Reservation reservation = Reservation.builder()
                .guest(guestUser)
                .host(hostUser)
                .car(car)
                .rentDate(createReservationRequestDto.getRentDate())
                .returnDate(createReservationRequestDto.getReturnDate())
                .status(ReservationStatus.READY)
                .build();
        reservationRepository.save(reservation);

        return ResponseDto.success(HttpStatus.OK);
    }

    public ResponseDto getGuestReservations() {
        // TODO: JWT 토큰에서 userId를 추출하고 로그인한 상태에서만 실행되도록 추가
        long guestUserId = 13L;

        List<Reservation> reservationEntities = reservationRepository.findAllByGuest_id(guestUserId);
        List<GetGuestReservationResponseDto> getGuestReservationResponseDtos = new ArrayList<>();

        for (Reservation reservation : reservationEntities) {
            Car car = reservation.getCar();
            List<CarImage> imageEntities = imageRepository.findByCar_Id(car.getId());
            SubCategory subCategory = car.getSubCategory();
            User host = car.getOwner();

            GetCarSimpleResponseDto getCarSimpleResponseDto = GetCarSimpleResponseDto.builder()
                    .id(car.getId())
                    .name(subCategory.getName())
                    .imageUrl(imageEntities.get(0).getUrl()) // 대표 이미지 1장
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

        return DataResponseDto.of(new GetGuestReservationListResponseDto(getGuestReservationResponseDtos));
    }

    public ResponseDto getHostReservations() {
        // TODO: JWT 토큰에서 userId를 추출하고 로그인한 상태에서만 실행되도록 추가
        long hostUserId = 1L;

        List<Reservation> reservationEntities = reservationRepository.findAllByHost_id(hostUserId);
        List<GetHostReservationResponseDto> getHostReservationResponseDtos = new ArrayList<>();

        for (Reservation reservation : reservationEntities) {
            Car car = reservation.getCar();
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

        return DataResponseDto.of(new GetHostReservationListResponseDto(getHostReservationResponseDtos));
    }

    @Transactional
    public ResponseDto cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new GeneralException(ErrorCode.RESERVATION_NOT_FOUND));

        reservation.setStatus(ReservationStatus.CANCEL);
        reservationRepository.save(reservation);

        return ResponseDto.success(HttpStatus.OK);
    }
}
