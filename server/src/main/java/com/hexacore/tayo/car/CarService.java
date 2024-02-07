package com.hexacore.tayo.car;

import com.hexacore.tayo.car.model.CarEntity;
import com.hexacore.tayo.car.model.CategoryDto;
import com.hexacore.tayo.car.model.CategoryListDto;
import com.hexacore.tayo.car.model.DateListDto;
import com.hexacore.tayo.common.DataResponseDto;
import com.hexacore.tayo.common.ResponseCode;
import com.hexacore.tayo.common.ResponseDto;
import com.hexacore.tayo.common.errors.GeneralException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final ModelRepository modelRepository;

    /* 모델, 세부 모델명 조회 */
    public ResponseDto getCategories() {
        List<CategoryDto> models = modelRepository.findAll().stream()
                .map(CategoryDto::new)
                .toList();
        return DataResponseDto.of(new CategoryListDto(models), "세부 모델 조회가 완료되었습니다.");
    }

    /* 예약 가능 날짜 수정 */
    public ResponseDto updateDates(Long carId, DateListDto dateListDto) {
        CarEntity car = carRepository.findById(carId)
                // 차량 조회가 안 되는 경우
                .orElseThrow(() -> new GeneralException(ResponseCode.NOT_FOUND, "존재하지 않는 id입니다"));

        car.setDates(dateListDto.getDates());
        carRepository.save(car);

        return ResponseDto.success(ResponseCode.OK, "예약 가능 날짜 수정이 완료되었습니다");
    }
}
