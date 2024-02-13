package com.hexacore.tayo.car;

import com.hexacore.tayo.car.dto.UpdateCarRequestDto;
import com.hexacore.tayo.car.dto.GetDateListRequestDto;
import com.hexacore.tayo.car.dto.CreateCarRequestDto;
import com.hexacore.tayo.common.response.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
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
        ResponseDto responseDto = carService.getSubCategories();
        return new ResponseEntity<>(responseDto, HttpStatusCode.valueOf(responseDto.getCode()));
    }

    @PostMapping
    public ResponseEntity<ResponseDto> createCar(HttpServletRequest request,
            @ModelAttribute CreateCarRequestDto CreateCarRequestDto) {
        Long userId = (Long) request.getAttribute("userId");
        ResponseDto responseDto = carService.createCar(CreateCarRequestDto, userId);

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
    public ResponseEntity<ResponseDto> carUpdate(@PathVariable Long carId,
            @ModelAttribute UpdateCarRequestDto updateCarRequestDto) {
        ResponseDto responseDto = carService.carUpdate(carId, updateCarRequestDto);
        return new ResponseEntity<>(responseDto, HttpStatusCode.valueOf(responseDto.getCode()));
    }

    @PutMapping("{carId}/date")
    public ResponseEntity<ResponseDto> updateDates(@PathVariable Long carId,
            @RequestBody GetDateListRequestDto getDateListRequestDto) {
        ResponseDto responseDto = carService.updateDates(carId, getDateListRequestDto);
        return new ResponseEntity<>(responseDto, HttpStatusCode.valueOf(responseDto.getCode()));
    }
}
