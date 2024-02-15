package com.hexacore.tayo.car;

import static org.mockito.BDDMockito.given;

import com.amazonaws.services.s3.AmazonS3;
import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.common.Position;
import com.hexacore.tayo.car.dto.CreateCarRequestDto;
import com.hexacore.tayo.category.CategoryRepository;
import com.hexacore.tayo.category.SubCategoryRepository;
import com.hexacore.tayo.category.model.SubCategory;
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
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
public class CarServiceCreateCarTest {

    @Mock
    private S3Manager mockS3Manager;
    @Mock
    private CarImageRepository carImageRepository;
    @Mock
    private CarRepository mockCarRepository;
    @Mock
    private CategoryRepository mockCategoryRepository;
    @Mock
    private SubCategoryRepository mockSubCategoryRepository;
    @InjectMocks
    private CarService carService;


    @Test
    @DisplayName("새로운 차량을 등록하는 경우 CarRepository.save() 메소드가 호출된다")
    void createCar_create() throws MalformedURLException {
        // given
        CreateCarRequestDto createCarRequestDto = new CreateCarRequestDto("11주 1111", "모델명 서브모델명", 10.0, "휘발유", "경차", 2,
                2020, 10000, "경기도 테스트 주소",
                new Position(10.0, 10.0), "설명",
                List.of(new MockMultipartFile("image1", "filename1.png", "image/png", "dummy".getBytes()),
                        new MockMultipartFile("image2", "filename2.png", "image/png", "dummy".getBytes()),
                        new MockMultipartFile("image3", "filename3.png", "image/png", "dummy".getBytes()),
                        new MockMultipartFile("image4", "filename4.png", "image/png", "dummy".getBytes()),
                        new MockMultipartFile("image5", "filename5.png", "image/png", "dummy".getBytes())),
                List.of(1, 2, 3, 4, 5));

        given(mockCarRepository.findByOwner_IdAndIsDeletedFalse(0L)).willReturn(Collections.emptyList());
        given(mockSubCategoryRepository.findByName(createCarRequestDto.getCarName()))
                .willReturn(Optional.of(SubCategory.builder().name("서브모델명").build()));
        given(mockCarRepository.findByOwner_IdAndCarNumberAndIsDeletedTrue(0L, createCarRequestDto.getCarNumber()))
                .willReturn(Optional.empty());
        given(mockS3Manager.uploadImage(Mockito.any())).willReturn("url");

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
                List.of(new MockMultipartFile("image1", "filename1.png", "image/png", "dummy".getBytes()),
                        new MockMultipartFile("image2", "filename2.png", "image/png", "dummy".getBytes()),
                        new MockMultipartFile("image3", "filename3.png", "image/png", "dummy".getBytes()),
                        new MockMultipartFile("image4", "filename4.png", "image/png", "dummy".getBytes()),
                        new MockMultipartFile("image5", "filename5.png", "image/png", "dummy".getBytes())),
                List.of(1, 2, 3, 4, 5));

        given(mockCarRepository.findByOwner_IdAndIsDeletedFalse(0L)).willReturn(Collections.emptyList());
        given(mockCarRepository.findByCarNumberAndIsDeletedFalse(createCarRequestDto.getCarNumber()))
                .willReturn(Collections.emptyList());
        given(mockSubCategoryRepository.findByName(createCarRequestDto.getCarName()))
                .willReturn(Optional.of(SubCategory.builder().name("모델명").build()));
        given(mockCarRepository.findByOwner_IdAndCarNumberAndIsDeletedTrue(0L, createCarRequestDto.getCarNumber()))
                .willReturn(Optional.of(new Car())); //
        given(mockS3Manager.uploadImage(Mockito.any())).willReturn("url");

        // when
        carService.createCar(createCarRequestDto, 0L);

        // then
        BDDMockito.verify(mockCarRepository, Mockito.times(0)).save(Mockito.any(Car.class));
    }

    @Test
    @DisplayName("차량의 모델명이 등록되어 있지 않은 경우 CAR_MODEL_NOT_FOUND 에러가 발생한다")
    void createCar_throwCarModelNotFound() {
        // given
        CreateCarRequestDto createCarRequestDto = new CreateCarRequestDto("11주 1111", "모델명 서브모델명", 10.0, "휘발유", "경차", 2,
                2020, 10000, "경기도 테스트 주소",
                new Position(10.0, 10.0), "설명",
                List.of(new MockMultipartFile("image1", "filename1.png", "image/png", "dummy image".getBytes())),
                List.of(1));

        given(mockCarRepository.findByOwner_IdAndIsDeletedFalse(0L)).willReturn(Collections.emptyList());
        given(mockCarRepository.findByCarNumberAndIsDeletedFalse(createCarRequestDto.getCarNumber()))
                .willReturn(Collections.emptyList());
        given(mockSubCategoryRepository.findByName(createCarRequestDto.getCarName()))
                .willReturn(Optional.empty());

        // when & then
        Assertions.assertThatThrownBy(() -> carService.createCar(createCarRequestDto, 0L))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorCode.CAR_MODEL_NOT_FOUND.getErrorMessage());
    }

    @Test
    @DisplayName("유저가 이미 등록한 차량이 있는 경우 USER_ALREADY_HAS_CAR 에러가 발생한다")
    void createCar_throwUserAlreadyHasCar() {
        // given
        CreateCarRequestDto createCarRequestDto = new CreateCarRequestDto("11주 1111", "모델명 서브모델명", 10.0, "휘발유", "경차", 2,
                2020, 10000, "경기도 테스트 주소",
                new Position(10.0, 10.0), "설명",
                List.of(new MockMultipartFile("image1", "filename1.png", "image/png", "dummy image".getBytes())),
                List.of(1));

        given(mockCarRepository.findByOwner_IdAndIsDeletedFalse(0L)).willReturn(List.of(new Car()));

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
                List.of(new MockMultipartFile("image1", "filename1.png", "image/png", "dummy image".getBytes())),
                List.of(1));

        given(mockCarRepository.findByOwner_IdAndIsDeletedFalse(0L)).willReturn(Collections.emptyList());
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
                List.of(new MockMultipartFile("image1", "filename1.png", "image/png", "dummy image".getBytes())),
                List.of(1, 2, 3));

        given(mockCarRepository.findByOwner_IdAndIsDeletedFalse(0L)).willReturn(Collections.emptyList());
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
                List.of(new MockMultipartFile("image1", "filename1.png", "image/png", "dummy image".getBytes())),
                List.of(1));

        given(mockCarRepository.findByOwner_IdAndIsDeletedFalse(0L)).willReturn(Collections.emptyList());
        given(mockCarRepository.findByCarNumberAndIsDeletedFalse(createCarRequestDto.getCarNumber()))
                .willReturn(Collections.emptyList());
        given(mockSubCategoryRepository.findByName(createCarRequestDto.getCarName()))
                .willReturn(Optional.of(SubCategory.builder().name("세부모델명").build()));
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
                List.of(new MockMultipartFile("image1", "filename1.txt", "text/plain", "dummy".getBytes()),
                        new MockMultipartFile("image2", "filename2.txt", "text/plain", "dummy".getBytes()),
                        new MockMultipartFile("image3", "filename3.txt", "text/plain", "dummy".getBytes()),
                        new MockMultipartFile("image4", "filename4.txt", "text/plain", "dummy".getBytes()),
                        new MockMultipartFile("image5", "filename5.txt", "text/plain", "dummy".getBytes())),
                List.of(1, 2, 3, 4, 5));

        given(mockCarRepository.findByOwner_IdAndIsDeletedFalse(0L)).willReturn(Collections.emptyList());
        given(mockCarRepository.findByCarNumberAndIsDeletedFalse(createCarRequestDto.getCarNumber()))
                .willReturn(Collections.emptyList());

        // when & then
        Assertions.assertThatThrownBy(() -> carService.createCar(createCarRequestDto, 0L))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorCode.INVALID_FUEL_TYPE.getErrorMessage());
    }

    @Test
    @DisplayName("지원하지 않는 차량 타입인 경우 INVALID_Car_TYPE 에러가 발생한다")
    void createCar_throwInvalidCarType() {
        // given
        CreateCarRequestDto createCarRequestDto = new CreateCarRequestDto("11주 1111", "서브모델명", 10.0, "휘발유", "중중형차", 2,
                2020, 10000, "경기도 테스트 주소",
                new CreatePositionRequestDto(10.0, 10.0), "설명",
                List.of(new MockMultipartFile("image1", "filename1.txt", "text/plain", "dummy".getBytes()),
                        new MockMultipartFile("image2", "filename2.txt", "text/plain", "dummy".getBytes()),
                        new MockMultipartFile("image3", "filename3.txt", "text/plain", "dummy".getBytes()),
                        new MockMultipartFile("image4", "filename4.txt", "text/plain", "dummy".getBytes()),
                        new MockMultipartFile("image5", "filename5.txt", "text/plain", "dummy".getBytes())),
                List.of(1, 2, 3, 4, 5));

        given(mockCarRepository.findByOwner_IdAndIsDeletedFalse(0L)).willReturn(Collections.emptyList());
        given(mockCarRepository.findByCarNumberAndIsDeletedFalse(createCarRequestDto.getCarNumber()))
                .willReturn(Collections.emptyList());

        // when & then
        Assertions.assertThatThrownBy(() -> carService.createCar(createCarRequestDto, 0L))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorCode.INAVALID_CAR_TYPE.getErrorMessage());
    }
}
