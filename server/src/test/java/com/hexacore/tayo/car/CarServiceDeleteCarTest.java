package com.hexacore.tayo.car;

import com.hexacore.tayo.car.model.CarEntity;
import com.hexacore.tayo.car.model.ImageEntity;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
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
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
public class CarServiceDeleteCarTest {

    @Mock
    private CarRepository carRepository;
    @Mock
    private ImageRepository imageRepository;
    @InjectMocks
    private CarService carService;

    @Test
    @DisplayName("차량 삭제 요청을 하면 차량과 이미지를 soft delete 한다")
    void deleteCar() {
        // given
        Long carId = 0L;
        BDDMockito.given(carRepository.findById(carId)).willReturn(Optional.of(new CarEntity()));
        BDDMockito.given(imageRepository.findByCar_Id(Mockito.any()))
                .willReturn(List.of(new ImageEntity(), new ImageEntity()));

        // when
        ResponseDto response = carService.deleteCar(carId);

        // then
        BDDMockito.verify(carRepository, Mockito.times(1)).save(Mockito.any(CarEntity.class));
        BDDMockito.verify(imageRepository, Mockito.times(2)).save(Mockito.any(ImageEntity.class));
        Assertions.assertThat(response.getSuccess()).isTrue();
        Assertions.assertThat(response.getCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("존재하지 않는 차량 Id를 삭제하는 경우 CAR_NOT_FOUND 에러가 발생한다")
    void deleteCar_throwCarNotFound() {
        // given
        Long carId = 0L;
        BDDMockito.given(carRepository.findById(carId)).willReturn(Optional.empty());

        // when & then
        Assertions.assertThatThrownBy(() -> carService.deleteCar(carId))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorCode.CAR_NOT_FOUND.getErrorMessage());
    }
}
