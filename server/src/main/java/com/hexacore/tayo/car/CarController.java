package com.hexacore.tayo.car;

import com.hexacore.tayo.car.model.DateListDto;
import com.hexacore.tayo.common.ResponseCode;
import com.hexacore.tayo.common.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cars")
public class CarController {

    @Autowired
    private CarService carService;


    @GetMapping("{carId}")
    public ResponseEntity<ResponseDto> carDetail(@PathVariable("carId") Long carId) {
        ResponseDto responseDto = carService.carDetail(carId);
        return new ResponseEntity<>(responseDto, ResponseCode.valueOf(responseDto.getCode()));
    }

    @PutMapping("{carId}/date")
    public ResponseEntity<ResponseDto> updateDates(@PathVariable("carId") Long carId, @RequestBody DateListDto dateListDto) {
        ResponseDto responseDto = carService.updateDates(carId, dateListDto);
        return new ResponseEntity<>(responseDto, ResponseCode.valueOf(responseDto.getCode()));
    }
}
