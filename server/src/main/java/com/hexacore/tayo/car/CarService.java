package com.hexacore.tayo.car;

import com.hexacore.tayo.car.model.*;
import com.hexacore.tayo.common.DataResponseDto;
import com.hexacore.tayo.common.ResponseCode;
import com.hexacore.tayo.common.ResponseDto;
import com.hexacore.tayo.common.errors.GeneralException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final ImageRepository imageRepository;

    /* 차량 정보 조회 */
    public DataResponseDto carDetail(Long carId) {
        CarEntity car = carRepository.findById(carId)
                // 차량 조회가 안 되는 경우
                .orElseThrow(() -> new GeneralException(ResponseCode.NOT_FOUND, "존재하지 않는 id입니다"));
        List<String> images = carDateList(carId);
        return DataResponseDto.of(new CarDto(car,images));
    }

    /* 차량 정보 수정 */
    public ResponseDto carUpdate(Long carId, CarUpdateDto carUpdateDto){
        CarEntity car = carRepository.findById(carId)
                // 차량 조회가 안 되는 경우
                .orElseThrow(() -> new GeneralException(ResponseCode.NOT_FOUND, "존재하지 않는 id입니다"));
        car.setFeePerHour(carUpdateDto.getFeePerHour());
        car.setAddress(carUpdateDto.getAddress());
        car.setPosition(carUpdateDto.getPosition().toEntity());
        car.setDescription(carUpdateDto.getDescription());
        
       //updateImage(carId,carUpdateDto.getImageUrls());

        return ResponseDto.success(ResponseCode.OK, "차량 수정이 완료되었습니다.");
    }

    /* 에약 가능 날짜 조회 */
    private List<String> carDateList(Long carId){
        return imageRepository.findAllByCar_IdAndIsDeletedFalseOrderByOrderIdxAsc(carId)
                .stream()
                .map(ImageEntity::getUrl)
                .collect(Collectors.toList());
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

    /* 차량 이미지 조회 */
    private void updateImage(Long carId, List<String> images){
        List<ImageEntity> carImages = imageRepository.findAllByCar_IdAndIsDeletedFalseOrderByOrderIdxAsc(carId);
        for (int i = 0;i<carImages.size();i++) {
            ImageEntity carImage = carImages.get(i);
            String url = images.get(i);
            if(!carImage.getUrl().equals(url)){
                imageRepository.updateUrlById(carImage.getId(),url);
            }
        }
    }
}
