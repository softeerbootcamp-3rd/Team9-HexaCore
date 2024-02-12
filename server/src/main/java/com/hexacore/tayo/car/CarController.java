package com.hexacore.tayo.car;

import com.hexacore.tayo.car.model.CarUpdateDto;
import com.hexacore.tayo.car.model.DateListDto;
import com.hexacore.tayo.car.model.PostCarDto;
import com.hexacore.tayo.common.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;

    @GetMapping("/categories")
    public ResponseEntity<ResponseDto> getCategories() {
        ResponseDto responseDto = carService.getCategories();
        return new ResponseEntity<>(responseDto, HttpStatusCode.valueOf(responseDto.getCode()));
    }

    @PostMapping()
    public ResponseEntity<ResponseDto> createCar(@Valid @ModelAttribute PostCarDto postCarDto) {
        // TODO: JWT 토큰에서 userId 가져와서 로그인한 경우에만 실행되도록
        ResponseDto responseDto = carService.createCar(postCarDto, null);
        return new ResponseEntity<>(responseDto, HttpStatusCode.valueOf(responseDto.getCode()));
    }

    @DeleteMapping("/{carId}")
    public ResponseEntity<ResponseDto> deleteCar(@PathVariable Long carId) {
        ResponseDto responseDto = carService.deleteCar(carId);
        return new ResponseEntity<>(responseDto, HttpStatusCode.valueOf(responseDto.getCode()));
    }


    @GetMapping("{carId}")
    public ResponseEntity<ResponseDto> carDetail(@PathVariable Long carId) {
        ResponseDto responseDto = carService.carDetail(carId);
        return new ResponseEntity<>(responseDto, HttpStatusCode.valueOf(responseDto.getCode()));
    }

    @PutMapping("{carId}")
    public ResponseEntity<ResponseDto> carUpdate(@PathVariable Long carId, @ModelAttribute CarUpdateDto carUpdateDto) {
        ResponseDto responseDto = carService.carUpdate(carId, carUpdateDto);
        return new ResponseEntity<>(responseDto, HttpStatusCode.valueOf(responseDto.getCode()));
    }

    @PutMapping("{carId}/date")
    public ResponseEntity<ResponseDto> updateDates(@PathVariable Long carId, @RequestBody DateListDto dateListDto) {
        ResponseDto responseDto = carService.updateDates(carId, dateListDto);
        return new ResponseEntity<>(responseDto, HttpStatusCode.valueOf(responseDto.getCode()));
    }
}
