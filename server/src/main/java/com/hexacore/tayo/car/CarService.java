package com.hexacore.tayo.car;

import com.hexacore.tayo.car.model.CarEntity;
import com.hexacore.tayo.car.model.DateListDto;
import com.hexacore.tayo.common.ResponseDto;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    /* 예약 가능 날짜 수정 */
    public ResponseDto updateDates(Long carId, DateListDto dateListDto) {
        CarEntity car = carRepository.findById(carId)
                // 차량 조회가 안 되는 경우
                .orElseThrow(() -> new GeneralException(ErrorCode.CAR_NOT_FOUND));

        car.setDates(dateListDto.getDates());
        carRepository.save(car);

        return ResponseDto.success(HttpStatus.ACCEPTED);
    }
}
