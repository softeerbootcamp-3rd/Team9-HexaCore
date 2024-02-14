package com.hexacore.tayo.car;

import static org.awaitility.Awaitility.given;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.hexacore.tayo.car.dto.CreatePositionRequestDto;
import com.hexacore.tayo.car.dto.UpdateCarRequestDto;
import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.car.model.CarImage;
import com.hexacore.tayo.car.model.CarType;
import com.hexacore.tayo.car.model.FuelType;
import com.hexacore.tayo.category.model.Category;
import com.hexacore.tayo.category.model.SubCategory;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.user.model.User;
import com.hexacore.tayo.util.S3Manager;
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
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)

public class CarServiceUpdateCarTest {

    @Mock
    private CarRepository carRepository;
    @Mock
    private S3Manager s3Manager;
    @Mock
    private CarImageRepository carImageRepository;

    @InjectMocks
    private CarService carService;

    User user = new User(1L, "jomualgy988@gmail.com", "1234", "김지훈", "010-4825-9803", null, null, false);
    Category category = new Category(1L, "i30");
    SubCategory subCategory = new SubCategory(1L, "i30 (PD)", category);
    GeometryFactory geometryFactory = new GeometryFactory();
    Coordinate coordinate = new Coordinate(120.0, 30.0);
    Point point = geometryFactory.createPoint(coordinate);
    Car car = new Car(1L, user, subCategory, "11가 1111", 14.3, FuelType.DIESEL, CarType.RV, 5, 2024, 10000,
            "경기도 용인시 기흥구 신정로", point, "설명", null, null, null);
    List<CarImage> images = List.of(new CarImage(1L, car, "filename1.png", 0, false),
            new CarImage(2L, car, "filename2.png", 1, false),
            new CarImage(3L, car, "filename3.png", 2, false),
            new CarImage(4L, car, "filename4.png", 3, false),
            new CarImage(5L, car, "filename5.png", 4, false));
    CreatePositionRequestDto position = new CreatePositionRequestDto(23.4, 32.4);

    @Test
    @DisplayName("차량 수정 성공")
    void UpdateCarSuccesTest() {
        //given
        Long carId = 1L;
        UpdateCarRequestDto updateCarRequestDto = new UpdateCarRequestDto(120000, "경기도 어쩌고 저쩌고", position, "설명 수정",
                List.of(new MockMultipartFile("image1", "filename1.png", "image/png", "dummy".getBytes()),
                        new MockMultipartFile("image2", "fileChangename2.png", "image/png", "dummy".getBytes()),
                        new MockMultipartFile("image3", "fileChangename3.png", "image/png", "dummy".getBytes()),
                        new MockMultipartFile("image4", "fileChangename4.png", "image/png", "dummy".getBytes()),
                        new MockMultipartFile("image5", "fileChangename5.png", "image/png", "dummy".getBytes())),
                List.of(1, 2, 3, 4, 5));

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carImageRepository.existsByCar_Id(1L)).thenReturn(Boolean.TRUE);
        when(s3Manager.uploadImage(Mockito.any())).thenReturn("/filename");

        //when
        carService.updateCar(1L, updateCarRequestDto, 1L);

        //then
        BDDMockito.verify(carRepository, Mockito.times(1)).save(Mockito.any(Car.class));
    }

    @Test
    @DisplayName("차량 조회 실패")
    void UpdateCarNotFoundTest() {
        //given
        Long carId = 2L;
        UpdateCarRequestDto updateCarRequestDto = new UpdateCarRequestDto(null, "경기도 어쩌고 저쩌고", position, "설명 수정",
                List.of(new MockMultipartFile("image1", "filename1.png", "image/png", "dummy".getBytes()),
                        new MockMultipartFile("image2", "fileChangename2.png", "image/png", "dummy".getBytes()),
                        new MockMultipartFile("image3", "fileChangename3.png", "image/png", "dummy".getBytes()),
                        new MockMultipartFile("image4", "fileChangename4.png", "image/png", "dummy".getBytes()),
                        new MockMultipartFile("image5", "fileChangename5.png", "image/png", "dummy".getBytes())),
                List.of(1, 2, 3, 4, 5));

        //when & then
        Assertions.assertThatThrownBy(() -> carService.updateCar(2L, updateCarRequestDto, 1L))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorCode.CAR_NOT_FOUND.getErrorMessage());
    }

    @Test
    @DisplayName("이미지 인덱스 리스트와 이미지 파일 리스트의 길이가 다를때")
    void UpdateCarEmptyFieldTest() {
        //given
        Long carId = 1L;
        UpdateCarRequestDto updateCarRequestDto = new UpdateCarRequestDto(12000, "경기도 어쩌고 저쩌고", position, "설명 수정",
                List.of(new MockMultipartFile("image1", "filename1.png", "image/png", "dummy".getBytes()),
                        new MockMultipartFile("image2", "fileChangename2.png", "image/png", "dummy".getBytes()),
                        new MockMultipartFile("image3", "fileChangename3.png", "image/png", "dummy".getBytes()),
                        new MockMultipartFile("image4", "fileChangename4.png", "image/png", "dummy".getBytes()),
                        new MockMultipartFile("image5", "fileChangename5.png", "image/png", "dummy".getBytes())),
                List.of(1, 2, 3, 4));

        //when & then
        Assertions.assertThatThrownBy(() -> carService.updateCar(1L, updateCarRequestDto, 1L))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorCode.IMAGE_INDEX_MISMATCH.getErrorMessage());
    }

    @Test
    @DisplayName("차량 소유주와 유저가 불일치 할떄")
    void UpdateCarUserNotHostTest() {
        //given
        Long carId = 1L;
        UpdateCarRequestDto updateCarRequestDto = new UpdateCarRequestDto(120000, "경기도 어쩌고 저쩌고", position, "설명 수정",
                List.of(new MockMultipartFile("image1", "filename1.png", "image/png", "dummy".getBytes()),
                        new MockMultipartFile("image2", "fileChangename2.png", "image/png", "dummy".getBytes()),
                        new MockMultipartFile("image3", "fileChangename3.png", "image/png", "dummy".getBytes()),
                        new MockMultipartFile("image4", "fileChangename4.png", "image/png", "dummy".getBytes()),
                        new MockMultipartFile("image5", "fileChangename5.png", "image/png", "dummy".getBytes())),
                List.of(1, 2, 3, 4, 5));

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        //when & then
        Assertions.assertThatThrownBy(() -> carService.updateCar(1L, updateCarRequestDto, 2L))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorCode.CAR_DATE_RANGE_UPDATED_BY_OTHERS.getErrorMessage());
    }
}
