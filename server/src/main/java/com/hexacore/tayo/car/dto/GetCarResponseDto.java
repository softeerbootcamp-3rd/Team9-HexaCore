package com.hexacore.tayo.car.dto;

import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.car.model.CarDateRange;
import com.hexacore.tayo.car.model.CarImage;
import com.hexacore.tayo.reservation.model.Reservation;
import com.hexacore.tayo.reservation.model.ReservationStatus;
import com.hexacore.tayo.user.dto.GetUserSimpleResponseDto;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class GetCarResponseDto {

    @NotNull
    private final Long id;

    @NotNull
    private final GetUserSimpleResponseDto host;

    @NotNull
    private final String carName;

    @NotNull
    private final String carNumber;

    @NotNull
    private final List<String> imageUrls;

    @NotNull
    private final Double mileage;

    @NotNull
    private final String fuel;

    @NotNull
    private final String type;

    @NotNull
    private final Integer capacity;

    @NotNull
    private final Integer year;

    @NotNull
    private final Integer feePerHour;

    @NotNull
    private final String address;

    @NotNull
    private final List<List<String>> carDateRanges;

    private final String description;

    private GetCarResponseDto(Car car, List<List<String>> carDateRanges) {
        this.id = car.getId();
        this.carName = car.getSubcategory().getName();
        this.carNumber = car.getCarNumber();
        this.imageUrls = car.getCarImages().stream()
                .sorted(Comparator.comparing(CarImage::getOrderIdx))
                .map(CarImage::getUrl).toList();
        this.mileage = car.getMileage();
        this.fuel = car.getFuel().getValue();
        this.type = car.getType().getValue();
        this.capacity = car.getCapacity();
        this.year = car.getYear();
        this.feePerHour = car.getFeePerHour();
        this.address = car.getAddress();
        this.description = car.getDescription();
        this.carDateRanges = carDateRanges;
        this.host = new GetUserSimpleResponseDto(car.getOwner());
    }

    public static GetCarResponseDto host(Car car) {
        return new GetCarResponseDto(car, getCarAvailableDatesForHost(car.getCarDateRanges()));
    }

    public static GetCarResponseDto guest(Car car) {
        return new GetCarResponseDto(car, getCarAvailableDatesForGuest(car));
    }

    private static List<List<String>> getCarAvailableDatesForHost(List<CarDateRange> carDateRanges) {
        List<List<String>> carAvailableDates = new ArrayList<>();

        for (CarDateRange carDateRange : carDateRanges) {
            LocalDate start = carDateRange.getStartDate();
            LocalDate end = carDateRange.getEndDate();

            // 만약 end가 현재 날짜보다 전일 경우 추가하지 않음
            if (end.isBefore(LocalDate.now())) {
                continue;
            }

            // 만약 start가 현재 날짜보다 전일 경우 start를 현재 날짜로 업데이트
            if (start.isBefore(LocalDate.now())) {
                start = LocalDate.now();
            }

            carAvailableDates.add(List.of(start.toString(), end.toString()));
        }

        return carAvailableDates.stream().sorted(Comparator.comparing(list -> list.get(0)))
                .collect(Collectors.toList());
    }

    private static List<List<String>> getCarAvailableDatesForGuest(Car car) {
        List<List<String>> result = new ArrayList<>();
        //가능일이 없으면 종료
        if (car.getCarDateRanges().isEmpty()) {
            return result;
        }

        List<CarDateRange> carAvailableDates = car.getCarDateRanges().stream()
                .filter(carDateRange -> carDateRange.getEndDate().isAfter(LocalDate.now()))
                .sorted(Comparator.comparing(CarDateRange::getStartDate))
                .toList();
        //status가 CANCEL이 아닌 예약 리스트를 시작날짜의 오름 차순으로 정렬
        List<Reservation> sortedReservations = car.getReservations().stream()
                .filter((reservation -> reservation.getStatus() != ReservationStatus.CANCEL))
                .filter(reservation -> reservation.getReturnDateTime().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(Reservation::getRentDateTime))
                .toList();

        int availableDatesIndex = 0;
        int reservationsIndex = 0;
        LocalDate start = carAvailableDates.get(0).getStartDate();
        LocalDate end;

        if (start.isBefore(LocalDate.now())) {
            start = LocalDate.now();
        }

        while (availableDatesIndex <= carAvailableDates.size() - 1
                && reservationsIndex <= sortedReservations.size() - 1) {

            //예약이 예약 가능일의 범위에 포함되지 않으면 다음 예약 가능일로 이동
            if (carAvailableDates.get(availableDatesIndex).getStartDate()
                    .isAfter(sortedReservations.get(reservationsIndex).getRentDateTime().toLocalDate())
                    || carAvailableDates.get(availableDatesIndex).getEndDate()
                    .isBefore(sortedReservations.get(reservationsIndex).getReturnDateTime().toLocalDate())) {
                end = carAvailableDates.get(availableDatesIndex).getEndDate();
                if (start.isBefore(end)) {
                    result.add(List.of(start.toString(), end.toString()));
                }
                start = carAvailableDates.get(++availableDatesIndex).getStartDate();
                continue;
            }

            end = sortedReservations.get(reservationsIndex).getRentDateTime().toLocalDate().minusDays(1);
            if (start.isBefore(end)) {
                result.add(List.of(start.toString(), end.toString()));
            }
            start = sortedReservations.get(reservationsIndex++).getReturnDateTime().toLocalDate().plusDays(1);
        }
        end = carAvailableDates.get(availableDatesIndex).getEndDate();
        if (start.isBefore(end)) {
            result.add(List.of(start.toString(), end.toString()));
        }

        return result;
    }

    @Override
    public String toString() {
        return "{"
                + "id=" + id
                + ", host=" + host
                + ", carName='" + carName + '\''
                + ", carNumber='" + carNumber + '\''
                + ", imageUrls=" + imageUrls
                + ", fuel='" + fuel + '\''
                + ", type='" + type + '\''
                + ", capacity=" + capacity
                + ", year=" + year
                + ", feePerHour=" + feePerHour
                + ", address='" + address + '\''
                + ", carDateRanges=" + carDateRanges
                + ", description='" + description + '\''
                + '}';
    }
}
