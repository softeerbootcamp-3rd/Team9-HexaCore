package com.hexacore.tayo.car;

import com.hexacore.tayo.car.model.CarEntity;
import com.hexacore.tayo.car.model.DateListDto;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
import jakarta.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Transactional
@SpringBootTest
class CarServiceUpdateDatesTest {

    @Autowired
    private CarService carService;

    @Autowired
    private CarRepository carRepository;

    @Test
    @DisplayName("특정 차량에 대해 예약 가능한 날짜를 업데이트 할 수 있다.")
    void valid_updateDates() throws ParseException {
        // given
        Long carId = carRepository.findAll().get(0).getId();
        List<List<Date>> dates = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        // 첫 번째 구간
        List<Date> firstInterval = new ArrayList<>();
        firstInterval.add(dateFormat.parse("2024-02-02T00:00:00"));
        firstInterval.add(dateFormat.parse("2024-02-10T23:59:59"));
        dates.add(firstInterval);

        // 두 번째 구간
        List<Date> secondInterval = new ArrayList<>();
        secondInterval.add(dateFormat.parse("2024-02-15T00:00:00"));
        secondInterval.add(dateFormat.parse("2024-02-20T23:59:59"));
        dates.add(secondInterval);

        DateListDto dateListDto = new DateListDto(dates);

        // when
        carService.updateDates(carId, dateListDto);

        // then
        CarEntity updatedCar = carRepository.findById(carId).orElse(new CarEntity());
        Assertions.assertEquals(dates, updatedCar.getDates());
    }

    @Test
    @DisplayName("존재하지 않는 차량 ID에 대해서는 예약 가능한 날짜를 수정할 수 없다.")
    void non_existing_carId() throws ParseException {
        // given
        Long carId = 999L;
        List<List<Date>> dates = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        // 첫 번째 구간
        List<Date> firstInterval = new ArrayList<>();
        firstInterval.add(dateFormat.parse("2024-02-02T00:00:00"));
        firstInterval.add(dateFormat.parse("2024-02-10T23:59:59"));
        dates.add(firstInterval);

        // 두 번째 구간
        List<Date> secondInterval = new ArrayList<>();
        secondInterval.add(dateFormat.parse("2024-02-15T00:00:00"));
        secondInterval.add(dateFormat.parse("2024-02-20T23:59:59"));
        dates.add(secondInterval);

        DateListDto dateListDto = new DateListDto(dates);

        // when
        GeneralException exception = Assertions.assertThrows(GeneralException.class,
                () -> carService.updateDates(carId, dateListDto));

        // then
        Assertions.assertEquals(ErrorCode.CAR_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("날짜 구간이 시작과 끝 2가지 Date로 이루어지지 않을 경우 예약 가능한 날짜를 수정할 수 없다.")
    void date_size_mismatch() throws ParseException {
        // given
        Long carId = carRepository.findAll().get(0).getId();
        List<List<Date>> dates = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        // 첫 번째 구간
        List<Date> firstInterval = new ArrayList<>();
        firstInterval.add(dateFormat.parse("2024-02-02T00:00:00"));
        firstInterval.add(dateFormat.parse("2024-02-10T23:59:59"));
        dates.add(firstInterval);

        // 두 번째 구간
        List<Date> secondInterval = new ArrayList<>();
        secondInterval.add(dateFormat.parse("2024-02-15T00:00:00"));
        dates.add(secondInterval);

        DateListDto dateListDto = new DateListDto(dates);

        // when
        GeneralException exception = Assertions.assertThrows(GeneralException.class,
                () -> carService.updateDates(carId, dateListDto));

        // then
        Assertions.assertEquals(ErrorCode.DATE_SIZE_MISMATCH, exception.getErrorCode());
    }

    @Test
    @DisplayName("예약 가능한 시작일이 끝나는 일 이후일 수 없다.")
    void start_date_after_end_date() throws ParseException {
        // given
        Long carId = carRepository.findAll().get(0).getId();
        List<List<Date>> dates = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        // 첫 번째 구간
        List<Date> firstInterval = new ArrayList<>();
        firstInterval.add(dateFormat.parse("2024-02-10T23:59:59"));
        firstInterval.add(dateFormat.parse("2024-02-02T00:00:00"));
        dates.add(firstInterval);

        // 두 번째 구간
        List<Date> secondInterval = new ArrayList<>();
        secondInterval.add(dateFormat.parse("2024-02-15T00:00:00"));
        secondInterval.add(dateFormat.parse("2024-02-20T23:59:59"));
        dates.add(secondInterval);

        DateListDto dateListDto = new DateListDto(dates);

        // when
        GeneralException exception = Assertions.assertThrows(GeneralException.class,
                () -> carService.updateDates(carId, dateListDto));

        // then
        Assertions.assertEquals(ErrorCode.START_DATE_AFTER_END_DATE, exception.getErrorCode());
    }
}
