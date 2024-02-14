package com.hexacore.tayo.car;

import com.hexacore.tayo.car.dto.CreateCarRequestDto;
import com.hexacore.tayo.car.dto.GetCarDateRangeRequestDto;
import com.hexacore.tayo.car.dto.GetCarResponseDto;
import com.hexacore.tayo.car.dto.UpdateCarRequestDto;
import com.hexacore.tayo.category.dto.GetSubCategoryListResponseDto;
import com.hexacore.tayo.common.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Response> getCategories() {
        GetSubCategoryListResponseDto getSubCategoryListResponseDto = carService.getSubCategories();
        return Response.of(HttpStatus.OK, getSubCategoryListResponseDto);
    }

    @PostMapping
    public ResponseEntity<Response> createCar(HttpServletRequest request,
            @ModelAttribute CreateCarRequestDto CreateCarRequestDto) {
        Long userId = (Long) request.getAttribute("userId");
        carService.createCar(CreateCarRequestDto, userId);

        return Response.of(HttpStatus.CREATED);
    }

    @DeleteMapping("/{carId}")
    public ResponseEntity<Response> deleteCar(@PathVariable Long carId) {
        carService.deleteCar(carId);
        return Response.of(HttpStatus.NO_CONTENT);
    }


    @GetMapping("{carId}")
    public ResponseEntity<Response> carDetail(@PathVariable Long carId) {
        GetCarResponseDto getCarResponseDto = carService.carDetail(carId);
        return Response.of(HttpStatus.OK, getCarResponseDto);
    }

    @PutMapping("{carId}")
    public ResponseEntity<Response> carUpdate(@PathVariable Long carId,
            @ModelAttribute UpdateCarRequestDto updateCarRequestDto) {
        carService.carUpdate(carId, updateCarRequestDto);
        return Response.of(HttpStatus.OK);
    }

    @PutMapping("{carId}/date")
    public ResponseEntity<Response> updateDateRanges(@PathVariable Long carId,
            HttpServletRequest request,
            @RequestBody GetCarDateRangeRequestDto getCarDateRangeRequestDto) {
        Long hostUserId = (Long) request.getAttribute("userId");
        carService.updateDateRanges(hostUserId, carId, getCarDateRangeRequestDto);
        return Response.of(HttpStatus.ACCEPTED);
    }
}
