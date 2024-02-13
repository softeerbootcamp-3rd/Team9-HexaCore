package com.hexacore.tayo.car;

import com.hexacore.tayo.car.dto.*;
import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.car.model.CarType;
import com.hexacore.tayo.car.model.SearchCarsDto;
import com.hexacore.tayo.category.dto.GetSubCategoryListResponseDto;
import com.hexacore.tayo.common.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;

    @GetMapping()
    public ResponseEntity<Response> getCars(
        @RequestParam double distance,
        @RequestParam double lat,
        @RequestParam double lng,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime rentDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime returnDate,
        @RequestParam int people,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) String category, // TODO: categoryId
        @RequestParam(required = false) int subCategoryId,
        @RequestParam(required = false) Integer minPrice,
        @RequestParam(required = false) Integer maxPrice,
        Pageable pageable
    ) {
        SearchCarsDto searchCarsDto = SearchCarsDto.builder()
                .distance(distance)
                .position(new CreatePositionRequestDto(lat, lng))
                .rentDate(rentDate)
                .returnDate(returnDate)
                .people(people)
                .type(CarType.valueOf(type))
                .category(category)
                .subCategoryId(subCategoryId)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .build();

        Page<Car> cars = carService.searchCars(searchCarsDto, pageable);
        return Response.of(HttpStatus.OK, cars);
    }

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
    public ResponseEntity<Response> updateDates(@PathVariable Long carId,
            @RequestBody GetDateListRequestDto getDateListRequestDto) {
        carService.updateDates(carId, getDateListRequestDto);
        return Response.of(HttpStatus.ACCEPTED);
    }
}
