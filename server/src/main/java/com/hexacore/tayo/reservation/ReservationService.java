package com.hexacore.tayo.reservation;

import com.hexacore.tayo.car.CarRepository;
import com.hexacore.tayo.car.ImageRepository;
import com.hexacore.tayo.car.model.CarEntity;
import com.hexacore.tayo.car.model.ImageEntity;
import com.hexacore.tayo.car.model.ModelEntity;
import com.hexacore.tayo.common.DataResponseDto;
import com.hexacore.tayo.common.ResponseDto;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.reservation.dto.CreateReservationDto;
import com.hexacore.tayo.reservation.dto.GetGuestReservationsResponseDto;
import com.hexacore.tayo.reservation.dto.GetHostReservationsResponseDto;
import com.hexacore.tayo.reservation.model.Guest;
import com.hexacore.tayo.reservation.model.GuestReservation;
import com.hexacore.tayo.reservation.model.HostCar;
import com.hexacore.tayo.reservation.model.HostReservation;
import com.hexacore.tayo.reservation.model.ReservationEntity;
import com.hexacore.tayo.reservation.model.ReservationStatus;
import com.hexacore.tayo.user.model.UserEntity;
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
    private final ImageRepository imageRepository;

    @Transactional
    public ResponseDto createReservation(CreateReservationDto createReservationDto) {
        long guestUserId = 13L; // TODO: JWT 토큰에서 userId를 추출하고 로그인한 상태에서만 실행되도록 추가
        // 현재 예시는 Guest 유저의 ID:13

        // TODO: userRepository에서 userEntity 제공받기
        UserEntity guestUser = UserEntity.builder().id(guestUserId).build();

        CarEntity carEntity = carRepository.findById(createReservationDto.getCarId())
                .orElseThrow(() -> new GeneralException(ErrorCode.CAR_NOT_FOUND));

        UserEntity hostUser = carEntity.getOwner();

        List<List<LocalDateTime>> possibleRentDates = carEntity.getDates();

        LocalDateTime rentDate = createReservationDto.getRentDate();
        LocalDateTime returnDate = createReservationDto.getReturnDate();

        possibleRentDates.stream()
                // date.get(0) ~ date.get(1) 사이의 날짜인지 검증
                .filter(date -> date.get(0).isBefore(rentDate))
                .filter(date -> date.get(1).isAfter(returnDate))
                .findFirst()
                .orElseThrow(() -> new GeneralException(ErrorCode.RESERVATION_DATE_NOT_IN_RANGE));

        ReservationEntity reservationEntity = ReservationEntity.builder()
                .guest(guestUser)
                .host(hostUser)
                .car(carEntity)
                .rentDate(createReservationDto.getRentDate())
                .returnDate(createReservationDto.getReturnDate())
                .status(ReservationStatus.READY)
                .build();
        reservationRepository.save(reservationEntity);

        return ResponseDto.success(HttpStatus.OK);
    }

    public ResponseDto getGuestReservations() {
        // TODO: JWT 토큰에서 userId를 추출하고 로그인한 상태에서만 실행되도록 추가
        long guestUserId = 13L;

        List<ReservationEntity> reservationEntities = reservationRepository.findAllByGuest_id(guestUserId);
        List<GuestReservation> guestReservations = new ArrayList<>();

        for (ReservationEntity reservationEntity : reservationEntities) {
            CarEntity carEntity = reservationEntity.getCar();
            List<ImageEntity> imageEntities = imageRepository.findByCar_Id(carEntity.getId());
            ModelEntity modelEntity = carEntity.getModel();
            UserEntity hostEntity = carEntity.getOwner();

            HostCar car = HostCar.builder()
                    .id(carEntity.getId())
                    .name(modelEntity.getCategory())
                    .imageUrl(imageEntities.get(0).getUrl()) // 대표 이미지 1장
                    .build();

            GuestReservation guestReservation = GuestReservation.builder()
                    .id(reservationEntity.getId())
                    .car(car)
                    .fee(carEntity.getFeePerHour())
                    .carAddress(carEntity.getAddress())
                    .rentDate(reservationEntity.getRentDate())
                    .returnDate(reservationEntity.getReturnDate())
                    .status(reservationEntity.getStatus())
                    .hostPhoneNumber(hostEntity.getPhoneNumber())
                    .build();

            guestReservations.add(guestReservation);
        }

        return DataResponseDto.of(new GetGuestReservationsResponseDto(guestReservations));
    }

    public ResponseDto getHostReservations() {
        // TODO: JWT 토큰에서 userId를 추출하고 로그인한 상태에서만 실행되도록 추가
        long hostUserId = 1L;

        List<ReservationEntity> reservationEntities = reservationRepository.findAllByHost_id(hostUserId);
        List<HostReservation> hostReservations = new ArrayList<>();

        for (ReservationEntity reservationEntity : reservationEntities) {
            CarEntity carEntity = reservationEntity.getCar();
            UserEntity guestEntity = reservationEntity.getGuest();

            Guest guest = Guest.builder()
                    .id(guestEntity.getId())
                    .nickname(guestEntity.getNickname())
                    .phoneNumber(guestEntity.getPhoneNumber())
                    .image(guestEntity.getProfileImg())
                    .build();

            HostReservation hostReservation = HostReservation.builder()
                    .id(reservationEntity.getId())
                    .guest(guest)
                    .rentDate(reservationEntity.getRentDate())
                    .returnDate(reservationEntity.getReturnDate())
                    .fee(carEntity.getFeePerHour())
                    .status(reservationEntity.getStatus())
                    .build();

            hostReservations.add(hostReservation);
        }

        return DataResponseDto.of(new GetHostReservationsResponseDto(hostReservations));
    }

    @Transactional
    public ResponseDto cancelReservation(Long reservationId) {
        ReservationEntity reservationEntity = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new GeneralException(ErrorCode.RESERVATION_NOT_FOUND));

        reservationEntity.setStatus(ReservationStatus.CANCEL);
        reservationRepository.save(reservationEntity);

        return ResponseDto.success(HttpStatus.OK);
    }
}
