package com.hexacore.tayo.car;

import com.hexacore.tayo.car.model.DateListDto;
import com.hexacore.tayo.car.model.PostCarDto;
import com.hexacore.tayo.common.ResponseCode;
import com.hexacore.tayo.common.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cars")
public class CarController {

    @Autowired
    private CarService carService;

    @GetMapping("/categories")
    public ResponseEntity<ResponseDto> getCategories() {
        ResponseDto responseDto = carService.getCategories();
        return new ResponseEntity<>(responseDto, ResponseCode.valueOf(responseDto.getCode()));
    }

    @PostMapping()
    public ResponseEntity<ResponseDto> createCar(@ModelAttribute PostCarDto postCarDto) {
        ResponseDto responseDto = carService.createCar(postCarDto);
        return new ResponseEntity<>(responseDto, ResponseCode.valueOf(responseDto.getCode()));
    }

    @PutMapping("{carId}/date")
    public ResponseEntity<ResponseDto> updateDates(@PathVariable Long carId, @RequestBody DateListDto dateListDto) {
        ResponseDto responseDto = carService.updateDates(carId, dateListDto);
        return new ResponseEntity<>(responseDto, ResponseCode.valueOf(responseDto.getCode()));
    }
}