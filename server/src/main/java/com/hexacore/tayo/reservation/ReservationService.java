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
import com.hexacore.tayo.reservation.model.CreateReservationDto;
import com.hexacore.tayo.reservation.model.GuestDto;
import com.hexacore.tayo.reservation.model.GuestReservationDto;
import com.hexacore.tayo.reservation.model.GuestReservationListDto;
import com.hexacore.tayo.reservation.model.HostCarDto;
import com.hexacore.tayo.reservation.model.HostReservationDto;
import com.hexacore.tayo.reservation.model.HostReservationListDto;
import com.hexacore.tayo.reservation.model.ReservationEntity;
import com.hexacore.tayo.reservation.model.ReservationStatus;
import com.hexacore.tayo.user.model.UserEntity;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

        List<List<Date>> possibleRentDates = carEntity.getDates();
        Date rentDate = createReservationDto.getRentDate();
        Date returnDate = createReservationDto.getReturnDate();

        List<Date> usingDate = possibleRentDates.stream()
                // date.get(0) ~ date.get(1) 사이의 날짜인지 검증
                .filter(date -> date.get(0).before(rentDate))
                .filter(date -> date.get(1).after(returnDate))
                .findFirst()
                .orElseThrow(() -> new GeneralException(ErrorCode.RESERVATION_DATE_NOT_IN_RANGE));

        carEntity.setDates(
                splitPossibleDates(possibleRentDates, usingDate)
        );
        carRepository.save(carEntity);

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
        List<GuestReservationDto> guestReservations = new ArrayList<>();

        for (ReservationEntity reservationEntity : reservationEntities) {
            CarEntity carEntity = reservationEntity.getCar();
            List<ImageEntity> imageEntities = imageRepository.findByCar_Id(carEntity.getId());
            ModelEntity modelEntity = carEntity.getModel();
            UserEntity hostEntity = carEntity.getOwner();

            HostCarDto car = HostCarDto.builder()
                    .id(carEntity.getId())
                    .name(modelEntity.getCategory())
                    .imageUrl(imageEntities.get(0).getUrl()) // 대표 이미지 1장
                    .build();

            GuestReservationDto guestReservation = GuestReservationDto.builder()
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

        return DataResponseDto.of(new GuestReservationListDto(guestReservations));
    }

    public ResponseDto getHostReservations() {
        // TODO: JWT 토큰에서 userId를 추출하고 로그인한 상태에서만 실행되도록 추가
        long hostUserId = 1L;

        List<ReservationEntity> reservationEntities = reservationRepository.findAllByHost_id(hostUserId);
        List<HostReservationDto> hostReservations = new ArrayList<>();

        for (ReservationEntity reservationEntity : reservationEntities) {
            CarEntity carEntity = reservationEntity.getCar();
            UserEntity guestEntity = reservationEntity.getGuest();

            GuestDto guest = GuestDto.builder()
                    .id(guestEntity.getId())
                    .nickname(guestEntity.getNickname())
                    .phoneNumber(guestEntity.getPhoneNumber())
                    .image(guestEntity.getProfileImg())
                    .build();

            HostReservationDto hostReservation = HostReservationDto.builder()
                    .id(reservationEntity.getId())
                    .guest(guest)
                    .rentDate(reservationEntity.getRentDate())
                    .returnDate(reservationEntity.getReturnDate())
                    .fee(carEntity.getFeePerHour())
                    .status(reservationEntity.getStatus())
                    .build();

            hostReservations.add(hostReservation);
        }

        return DataResponseDto.of(new HostReservationListDto(hostReservations));
    }

    @Transactional
    public ResponseDto cancelReservations(Long reservationId) {
        ReservationEntity reservationEntity = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new GeneralException(ErrorCode.RESERVATION_NOT_FOUND));
        CarEntity carEntity = reservationEntity.getCar();

        List<Date> canceledDate = List.of(reservationEntity.getRentDate(), reservationEntity.getReturnDate());

        carEntity.setDates(
                mergePossibleDates(carEntity.getDates(), canceledDate)
        );
        carRepository.save(carEntity);

        reservationEntity.setStatus(ReservationStatus.CANCEL);
        reservationRepository.save(reservationEntity);

        return ResponseDto.success(HttpStatus.OK);
    }

    private List<List<Date>> splitPossibleDates(List<List<Date>> possibleDates, List<Date> usingDateTime) {
        // 예약 일시를 예약 일자로 변경한다.
        Date usingStartDateTime = usingDateTime.get(0);

        Calendar startDate = Calendar.getInstance();
        startDate.setTime(usingStartDateTime);
        startDate.add(Calendar.DATE, -1);
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);

        Date usingEndDateTime = usingDateTime.get(1);

        Calendar endDate = Calendar.getInstance();
        endDate.setTime(usingEndDateTime);
        endDate.add(Calendar.DATE, 1);
        endDate.set(Calendar.HOUR_OF_DAY, 0);
        endDate.set(Calendar.MINUTE, 0);
        endDate.set(Calendar.SECOND, 0);

        // flatMap을 이용해서 예약 가능 일자에서 예약 신청을 한 일자를 제거하고 두 개의 요소로 나눈다.
        // 예를 들어 02-09 ~ 02-20 까지 예약이 가능하고 02-11 ~ 02-13까지 예약을 한다고 하면
        // [02-09 ~ 02-10, 02-14 ~ 02-20]로 나누고 이를 CarEntity에 저장한다.
        return possibleDates.stream()
                .flatMap(possibleDate -> {
                    Date possibleStartDate = possibleDate.get(0);
                    Date possibleEndDate = possibleDate.get(1);

                    // 예약 범위에 맞는 구간의 경우 두 개의 요소로 나눈다.
                    if (possibleStartDate.before(usingStartDateTime)
                            && possibleEndDate.after(usingEndDateTime)) {

                        List<Date> startSplit = possibleStartDate.before(startDate.getTime())
                                ? List.of(possibleStartDate, startDate.getTime()) : List.of();
                        List<Date> endSplit = possibleEndDate.after(endDate.getTime())
                                ? List.of(endDate.getTime(), possibleEndDate) : List.of();

                        return Stream.of(startSplit, endSplit).filter(dates -> !dates.isEmpty());
                    }
                    return Stream.of(possibleDate);
                })
                .collect(Collectors.toList());
    }

    private List<List<Date>> mergePossibleDates(List<List<Date>> possibleDates, List<Date> canceledDate) {
        // 취소한 일자에 대해 나누었던 일자를 합친다.
        // [02-09 ~ 02-10, 02-14 ~ 02-20, 02-23 ~ 03-10]인 예약 가능 일자가 있고
        // 02-11 ~ 02-13 에 예약한 일자를 취소한다면 합쳐서
        // [02-09 ~ 02-20, 02-23 ~ 03-10] 로 변경한다.
        // 이때 possibleDates 리스트는 무조건 정렬이 되어있어야 한다.
        LocalDate canceledStartDate = LocalDate.ofEpochDay(canceledDate.get(0).getTime());
        LocalDate canceledEndDate = LocalDate.ofEpochDay(canceledDate.get(1).getTime());
        List<List<Date>> result = new ArrayList<>();

        for (int idx = 0; idx < possibleDates.size() - 1; idx++) {
            List<Date> priorPossibleDate = possibleDates.get(idx);
            List<Date> laterPossibleDate = possibleDates.get(idx + 1);

            LocalDate priorPossibleStartDate = LocalDate.ofEpochDay(priorPossibleDate.get(0).getTime());
            LocalDate priorPossibleEndDate = LocalDate.ofEpochDay(priorPossibleDate.get(1).getTime());

            LocalDate laterPossibleStartDate = LocalDate.ofEpochDay(laterPossibleDate.get(0).getTime());
            LocalDate laterPossibleEndDate = LocalDate.ofEpochDay(laterPossibleDate.get(1).getTime());

            if (priorPossibleEndDate.plusDays(1).isEqual(canceledStartDate)
                    && canceledEndDate.plusDays(1).isEqual(laterPossibleStartDate)) {
                result.add(
                        List.of(new Date(priorPossibleStartDate.toEpochDay()),
                                new Date(laterPossibleEndDate.toEpochDay()))
                );
                idx++;
            } else {
                result.add(priorPossibleDate);
            }
        }

        return result;
    }
}
