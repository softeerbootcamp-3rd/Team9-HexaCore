package com.hexacore.tayo.car;

import com.hexacore.tayo.car.model.*;
import com.hexacore.tayo.common.DataResponseDto;
import com.hexacore.tayo.common.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;

    @GetMapping()
    public ResponseEntity<ResponseDto> getCars(
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
                .position(new PositionDto(lat, lng))
                .rentDate(rentDate)
                .returnDate(returnDate)
                .people(people)
                .type(CarType.valueOf(type))
                .category(category)
                .subCategoryId(subCategoryId)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .build();

        Page<CarEntity> carEntities = carService.searchCars(searchCarsDto, pageable);
        DataResponseDto responseDto = DataResponseDto.of(carEntities);
        return new ResponseEntity<>(responseDto, HttpStatusCode.valueOf(responseDto.getCode()));
    }

    @GetMapping("/categories")
    public ResponseEntity<ResponseDto> getCategories() {
        ResponseDto responseDto = carService.getCategories();
        return new ResponseEntity<>(responseDto, HttpStatusCode.valueOf(responseDto.getCode()));
    }

    @PostMapping()
    public ResponseEntity<ResponseDto> createCar(@ModelAttribute PostCarDto postCarDto) {
        ResponseDto responseDto = carService.createCar(postCarDto);
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
