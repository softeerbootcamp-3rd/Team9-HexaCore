package com.hexacore.tayo.car;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import com.hexacore.tayo.car.dto.GetDateListRequestDto;
import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
import jakarta.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

        GetDateListRequestDto dateListDto = new GetDateListRequestDto(dates);

        // when
        carService.updateDates(carId, dateListDto);

        // then
        Car updatedCar = carRepository.findById(carId).orElse(new Car());
        assertThat(dates).isEqualTo(updatedCar.getDates());
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

        GetDateListRequestDto dateListDto = new GetDateListRequestDto(dates);

        // when
        Throwable thrown = catchThrowable(() -> carService.updateDates(carId, dateListDto));

        // then
        assertThat(thrown)
                .as("존재하지 않는 차량 ID에 대한 예외 처리")
                .isInstanceOf(GeneralException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CAR_NOT_FOUND)
                .hasMessageContaining("존재하지 않는 차량입니다.");
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

        GetDateListRequestDto dateListDto = new GetDateListRequestDto(dates);

        // when
        Throwable thrown = catchThrowable(() -> carService.updateDates(carId, dateListDto));

        // then
        assertThat(thrown)
                .as("날짜 구간이 맞지 않는 상황에 대한 예외 처리")
                .isInstanceOf(GeneralException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DATE_SIZE_MISMATCH)
                .hasMessageContaining("날짜 구간이 맞지 않습니다.");
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

        GetDateListRequestDto dateListDto = new GetDateListRequestDto(dates);

        // when
        Throwable thrown = catchThrowable(() -> carService.updateDates(carId, dateListDto));

        // then
        assertThat(thrown)
                .as("시작 날짜가 끝 날짜보다 뒤인 경우에 대한 예외 처리")
                .isInstanceOf(GeneralException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.START_DATE_AFTER_END_DATE)
                .hasMessageContaining("예약 시작 날짜가 끝 날짜보다 뒤에 있을 수 없습니다.");
    }
}
