package com.hexacore.tayo.car;

import static org.mockito.BDDMockito.given;

import com.hexacore.tayo.car.carRepository.CarRepository;
import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.category.SubcategoryRepository;
import com.hexacore.tayo.category.model.Category;
import com.hexacore.tayo.category.model.Subcategory;
import com.hexacore.tayo.common.Position;
import com.hexacore.tayo.car.dto.CreateCarRequestDto;
import com.hexacore.tayo.category.CategoryRepository;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.util.S3Manager;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CarServiceCreateCarTest {

    @Mock
    private S3Manager mockS3Manager;
    @Mock
    private CarImageRepository mockCarImageRepository;
    @Mock
    private CarRepository mockCarRepository;
    @Mock
    private CategoryRepository mockCategoryRepository;
    @Mock
    private SubcategoryRepository mockSubcategoryRepository;
    @InjectMocks
    private CarService carService;


    @Test
    @DisplayName("새로운 차량을 등록하는 경우 CarRepository.save() 메소드가 호출된다")
    void createCar_create() throws MalformedURLException {
        // given
        CreateCarRequestDto createCarRequestDto = new CreateCarRequestDto("11주 1111", "모델명 서브모델명", 10.0, "휘발유", "경차", 2,
                2020, 10000, "경기도 테스트 주소",
                new Position(10.0, 10.0), "설명",
                List.of("https://1", "https://2", "https://3", "https://4", "https://5"),
                List.of(1, 2, 3, 4, 5));

        given(mockCarRepository.findByOwner_IdAndIsDeletedFalse(0L)).willReturn(Optional.empty());
        given(mockSubcategoryRepository.findByName(createCarRequestDto.getCarName()))
                .willReturn(Optional.of(Subcategory.builder().name("서브모델명").build()));
        given(mockCarRepository.findByOwner_IdAndCarNumberAndIsDeletedTrue(0L, createCarRequestDto.getCarNumber()))
                .willReturn(Optional.empty());

        // when
        carService.createCar(createCarRequestDto, 0L);

        // then
        BDDMockito.verify(mockCarRepository, Mockito.times(1)).save(Mockito.any(Car.class));

    }

    @Test
    @DisplayName("삭제한 차량을 재등록하는 경우 update 방식으로 동작한다")
    void createCar_update() throws MalformedURLException {
        // given
        CreateCarRequestDto createCarRequestDto = new CreateCarRequestDto("11주 1111", "모델명 서브모델명", 10.0, "휘발유", "경차", 2,
                2020, 10000, "경기도 테스트 주소",
                new Position(10.0, 10.0), "설명",
                List.of("https://1", "https://2", "https://3", "https://4", "https://5"),
                List.of(1, 2, 3, 4, 5));

        given(mockCarRepository.findByOwner_IdAndIsDeletedFalse(0L)).willReturn(Optional.empty());
        given(mockCarRepository.findByCarNumberAndIsDeletedFalse(createCarRequestDto.getCarNumber()))
                .willReturn(Collections.emptyList());
        given(mockSubcategoryRepository.findByName(createCarRequestDto.getCarName()))
                .willReturn(Optional.of(Subcategory.builder().name("모델명").build()));
        given(mockCarRepository.findByOwner_IdAndCarNumberAndIsDeletedTrue(0L, createCarRequestDto.getCarNumber()))
                .willReturn(Optional.of(new Car())); //

        // when
        carService.createCar(createCarRequestDto, 0L);

        // then
        BDDMockito.verify(mockCarRepository, Mockito.times(0)).save(Mockito.any(Car.class));
    }

    @Test
    @DisplayName("차량의 모델명이 등록되어 있지 않은 경우 해당 모델명을 새롭게 등록한다.")
    void createCar_throwCarModelNotFound() {
        // given
        CreateCarRequestDto createCarRequestDto = new CreateCarRequestDto("11주 1111", "모델명 서브모델명", 10.0, "휘발유", "경차", 2,
                2020, 10000, "경기도 테스트 주소",
                new Position(10.0, 10.0), "설명",
                List.of("https://1", "https://2", "https://3", "https://4", "https://5"),
                List.of(1, 2, 3, 4, 5));

        given(mockCarRepository.findByOwner_IdAndIsDeletedFalse(0L)).willReturn(Optional.empty());
        given(mockCarRepository.findByCarNumberAndIsDeletedFalse(createCarRequestDto.getCarNumber()))
                .willReturn(Collections.emptyList());
        given(mockSubcategoryRepository.findByName(createCarRequestDto.getCarName()))
                .willReturn(Optional.empty());
        given(mockCategoryRepository.findAll()).willReturn(List.of(Category.builder().name("ETC").build()));
        given(mockCarRepository.findByOwner_IdAndCarNumberAndIsDeletedTrue(0L, createCarRequestDto.getCarNumber()))
                .willReturn(Optional.empty()); //

        // when
        carService.createCar(createCarRequestDto, 0L);

        // when & then
        BDDMockito.verify(mockSubcategoryRepository, Mockito.times(1)).save(Mockito.any());
        BDDMockito.verify(mockCarRepository, Mockito.times(1)).save(Mockito.any(Car.class));
    }

    @Test
    @DisplayName("유저가 이미 등록한 차량이 있는 경우 USER_ALREADY_HAS_CAR 에러가 발생한다")
    void createCar_throwUserAlreadyHasCar() {
        // given
        CreateCarRequestDto createCarRequestDto = new CreateCarRequestDto("11주 1111", "모델명 서브모델명", 10.0, "휘발유", "경차", 2,
                2020, 10000, "경기도 테스트 주소",
                new Position(10.0, 10.0), "설명",
                List.of("https://1"),
                List.of(1));

        given(mockCarRepository.findByOwner_IdAndIsDeletedFalse(0L)).willReturn(Optional.of(new Car()));

        // when & then
        Assertions.assertThatThrownBy(() -> carService.createCar(createCarRequestDto, 0L))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorCode.USER_ALREADY_HAS_CAR.getErrorMessage());

    }

    @Test
    @DisplayName("중복되는 차량 번호를 등록한 경우 CAR_NUMBER_DUPLICATED 에러가 발생한다")
    void cretaeCar_throwCarNumberDuplicated() {
        // given
        CreateCarRequestDto createCarRequestDto = new CreateCarRequestDto("11주 1111", "서브모델명", 10.0, "휘발유", "경차", 2,
                2020, 10000, "경기도 테스트 주소",
                new Position(10.0, 10.0), "설명",
                List.of("https://1"),
                List.of(1));

        given(mockCarRepository.findByOwner_IdAndIsDeletedFalse(0L)).willReturn(Optional.empty());
        given(mockCarRepository.findByCarNumberAndIsDeletedFalse(createCarRequestDto.getCarNumber()))
                .willReturn(List.of(new Car()));

        // when & then
        Assertions.assertThatThrownBy(() -> carService.createCar(createCarRequestDto, 0L))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorCode.CAR_NUMBER_DUPLICATED.getErrorMessage());
    }

    @Test
    @DisplayName("입력받은 인덱스의 길이와 이미지의 길이가 같지 않은 경우 IMAGE_INDEX_MISMATCH 에러가 발생한다")
    void createCar_throwImageIndexMisMatch() {
        // given: 이미지의 길이는 1, 인덱스의 길이는 3
        CreateCarRequestDto createCarRequestDto = new CreateCarRequestDto("11주 1111", "서브모델명", 10.0, "휘발유", "경차", 2,
                2020, 10000, "경기도 테스트 주소",
                new Position(10.0, 10.0), "설명",
                List.of("https://1"),
                List.of(1, 2, 3));

        given(mockCarRepository.findByOwner_IdAndIsDeletedFalse(0L)).willReturn(Optional.empty());
        given(mockCarRepository.findByCarNumberAndIsDeletedFalse(createCarRequestDto.getCarNumber()))
                .willReturn(Collections.emptyList());

        // when & then
        Assertions.assertThatThrownBy(() -> carService.createCar(createCarRequestDto, 0L))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorCode.IMAGE_INDEX_MISMATCH.getErrorMessage());
    }

    @Test
    @DisplayName("차량의 이미지를 5개 미만으로 등록한 경우 CAR_IMAGE_INSUFFICIENT 에러가 발생한다")
    void createCar_throwCarImageInsufficient() {
        // given
        CreateCarRequestDto createCarRequestDto = new CreateCarRequestDto("11주 1111", "서브모델명", 10.0, "휘발유", "경차", 2,
                2020, 10000, "경기도 테스트 주소",
                new Position(10.0, 10.0), "설명",
                List.of("https://1"),
                List.of(1));

        given(mockCarRepository.findByOwner_IdAndIsDeletedFalse(0L)).willReturn(Optional.empty());
        given(mockCarRepository.findByCarNumberAndIsDeletedFalse(createCarRequestDto.getCarNumber()))
                .willReturn(Collections.emptyList());
        given(mockSubcategoryRepository.findByName(createCarRequestDto.getCarName()))
                .willReturn(Optional.of(Subcategory.builder().name("세부모델명").build()));
        given(mockCarRepository.findByOwner_IdAndCarNumberAndIsDeletedTrue(0L, createCarRequestDto.getCarNumber()))
                .willReturn(Optional.of(new Car())); //

        // when & then
        Assertions.assertThatThrownBy(() -> carService.createCar(createCarRequestDto, 0L))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorCode.CAR_IMAGE_INSUFFICIENT.getErrorMessage());
    }

    @Test
    @DisplayName("지원하지 않는 연료 타입인 경우 INVALID_FUEL_TYPE 에러가 발생한다")
    void createCar_throwInvalidFuelType() {
        // given
        CreateCarRequestDto createCarRequestDto = new CreateCarRequestDto("11주 1111", "서브모델명", 10.0, "가솔린", "경차", 2,
                2020, 10000, "경기도 테스트 주소",
                new Position(10.0, 10.0), "설명",
                List.of("https://1", "https://2", "https://3", "https://4", "https://5"),
                List.of(1, 2, 3, 4, 5));

        given(mockCarRepository.findByOwner_IdAndIsDeletedFalse(0L)).willReturn(Optional.empty());
        given(mockCarRepository.findByCarNumberAndIsDeletedFalse(createCarRequestDto.getCarNumber()))
                .willReturn(Collections.emptyList());
        given(mockSubcategoryRepository.findByName(createCarRequestDto.getCarName())).willReturn(Optional.of(new Subcategory()));

        // when & then
        Assertions.assertThatThrownBy(() -> carService.createCar(createCarRequestDto, 0L))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorCode.INVALID_FUEL_TYPE.getErrorMessage());
    }

    @Test
    @DisplayName("지원하지 않는 차량 타입인 경우 INVALID_CAR_TYPE 에러가 발생한다")
    void createCar_throwInvalidCarType() {
        // given
        CreateCarRequestDto createCarRequestDto = new CreateCarRequestDto("11주 1111", "서브모델명", 10.0, "휘발유", "중중형차", 2,
                2020, 10000, "경기도 테스트 주소",
                new Position(10.0, 10.0), "설명",
                List.of("https://1", "https://2", "https://3", "https://4", "https://5"),
                List.of(1, 2, 3, 4, 5));

        given(mockCarRepository.findByOwner_IdAndIsDeletedFalse(0L)).willReturn(Optional.empty());
        given(mockCarRepository.findByCarNumberAndIsDeletedFalse(createCarRequestDto.getCarNumber()))
                .willReturn(Collections.emptyList());
        given(mockSubcategoryRepository.findByName(createCarRequestDto.getCarName())).willReturn(Optional.of(new Subcategory()));

        // when & then
        Assertions.assertThatThrownBy(() -> carService.createCar(createCarRequestDto, 0L))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorCode.INVALID_CAR_TYPE.getErrorMessage());
    }
}
