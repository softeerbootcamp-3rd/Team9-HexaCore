package com.hexacore.tayo.car;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.hexacore.tayo.car.dto.GetCarResponseDto;
import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.car.model.CarImage;
import com.hexacore.tayo.car.model.CarType;
import com.hexacore.tayo.car.model.FuelType;
import com.hexacore.tayo.category.model.Category;
import com.hexacore.tayo.category.model.Subcategory;
import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.user.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class CarServiceRetrieveCarTest {

    @Mock
    private CarRepository carRepository;
    @Mock
    private CarImageRepository carImageRepository;

    @InjectMocks
    private CarService carService;

    User user = new User(1L, "jomualgy988@gmail.com", "1234", "김지훈", "010-4825-9803", null, null, false);
    Category category = Category.builder().id(1L).name("i30").build();
    Subcategory subcategory = new Subcategory(1L, "i30 (PD)", category);
    GeometryFactory geometryFactory = new GeometryFactory();
    Coordinate coordinate = new Coordinate(120.0, 30.0);
    Point point = geometryFactory.createPoint(coordinate);
    Car car = new Car(1L, user, subcategory, "11가 1111", 14.3, FuelType.DIESEL, CarType.RV, 5, 2024, 10000,
            "경기도 용인시 기흥구 신정로", point, "설명", null, null, null);
    List<CarImage> images = new ArrayList<>();

    @Test
    @DisplayName("차량 상세 조회 성공")
    public void carDetailSuccessTest() {
        //given
        Long carId = 1L;
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carImageRepository.findAllByCar_IdAndIsDeletedFalseOrderByOrderIdxAsc(1L)).thenReturn(images);
        //when
        GetCarResponseDto getCarResponseDto = carService.carDetail(carId);
        //then
        Assertions.assertThat(getCarResponseDto.getHost()).isNotNull();
        Assertions.assertThat(getCarResponseDto.getCarName()).isEqualTo(subcategory.getName());
        Assertions.assertThat(getCarResponseDto.getCarNumber()).isEqualTo("11가 1111");
        Assertions.assertThat(getCarResponseDto.getImageUrls()).isNotNull();
        Assertions.assertThat(getCarResponseDto.getMileage()).isEqualTo(14.3);
        Assertions.assertThat(getCarResponseDto.getFuel()).isEqualTo(FuelType.DIESEL.getValue());
        Assertions.assertThat(getCarResponseDto.getType()).isEqualTo(CarType.RV.getValue());
        Assertions.assertThat(getCarResponseDto.getCapacity()).isEqualTo(5);
        Assertions.assertThat(getCarResponseDto.getYear()).isEqualTo(2024);
        Assertions.assertThat(getCarResponseDto.getFeePerHour()).isEqualTo(10000);
        Assertions.assertThat(getCarResponseDto.getAddress()).isEqualTo("경기도 용인시 기흥구 신정로");
        Assertions.assertThat(getCarResponseDto.getDescription()).isEqualTo("설명");
    }

    @Test
    @DisplayName("차량 조회가 안되는 경우")
    public void carDetailNotFoundTest() {
        //given
        Long carId = 2L;
        //when & then
        assertThrows(GeneralException.class, () -> {
            carService.carDetail(carId);
        }, "Car not found should throw GeneralException with ErrorCode.CAR_NOT_FOUND");
    }
}
