package com.hexacore.tayo.car;

import com.hexacore.tayo.car.dto.CreateCarRequestDto;
import com.hexacore.tayo.car.dto.GetCarResponseDto;
import com.hexacore.tayo.car.dto.UpdateCarDateRangeRequestDto.CarDateRangesDto;
import com.hexacore.tayo.car.dto.UpdateCarRequestDto;
import com.hexacore.tayo.car.dto.*;
import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.car.model.CarType;
import com.hexacore.tayo.car.dto.SearchCarsParamsDto;
import com.hexacore.tayo.common.Position;
import com.hexacore.tayo.common.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;

    @GetMapping()
    public ResponseEntity<Response> getCars(
        @RequestParam Double distance,
        @RequestParam Double lat,
        @RequestParam Double lng,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate endDate,
        @RequestParam Integer party,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) Integer categoryId,
        @RequestParam(required = false) Integer subcategoryId,
        @RequestParam(required = false) Integer minPrice,
        @RequestParam(required = false) Integer maxPrice,
        Pageable pageable
    ) {
        SearchCarsParamsDto searchCarsParamsDto = SearchCarsParamsDto.builder()
                .distance(distance)
                .position(new Position(lat, lng))
                .startDate(startDate)
                .endDate(endDate)
                .party(party)
                .type(CarType.of(type))
                .categoryId(categoryId)
                .subcategoryId(subcategoryId)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .build();

        Page<Car> cars = carService.searchCars(searchCarsParamsDto, pageable);
        Page<SearchCarsResultDto> data = cars.map(SearchCarsResultDto::of);
        return Response.of(HttpStatus.OK, data);
    }

    @PostMapping
    public ResponseEntity<Response> createCar(HttpServletRequest request,
            @Valid @ModelAttribute CreateCarRequestDto createCarRequestDto) {
        Long userId = (Long) request.getAttribute("userId");
        carService.createCar(createCarRequestDto, userId);

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
    public ResponseEntity<Response> updateCar(@PathVariable Long carId,
            @Valid @ModelAttribute UpdateCarRequestDto updateCarRequestDto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        carService.updateCar(carId, updateCarRequestDto, userId);
        return Response.of(HttpStatus.OK);
    }

    @PutMapping("{carId}/date")
    public ResponseEntity<Response> updateDateRanges(@PathVariable Long carId,
            HttpServletRequest request,
            @RequestBody CarDateRangesDto getCarDateRangeRequestDto) {
        Long hostUserId = (Long) request.getAttribute("userId");
        carService.updateDateRanges(hostUserId, carId, getCarDateRangeRequestDto);
        return Response.of(HttpStatus.ACCEPTED);
    }
}
