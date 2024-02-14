package com.hexacore.tayo.car;

import com.hexacore.tayo.car.dto.CreateCarRequestDto;
import com.hexacore.tayo.car.dto.GetCarDateRangeRequestDto;
import com.hexacore.tayo.car.dto.GetCarResponseDto;
import com.hexacore.tayo.car.dto.UpdateCarRequestDto;
import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.car.model.CarType;
import com.hexacore.tayo.car.model.SearchCarsDto;
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

import java.time.LocalDate;

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
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate rentDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate returnDate,
        @RequestParam int people,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) int categoryId,
        @RequestParam(required = false) int subcategoryId,
        @RequestParam(required = false) Integer minPrice,
        @RequestParam(required = false) Integer maxPrice,
        Pageable pageable
    ) {
        SearchCarsDto searchCarsDto = SearchCarsDto.builder()
                .distance(distance)
                .position(new Position(lat, lng))
                .rentDate(rentDate)
                .returnDate(returnDate)
                .people(people)
                .type(CarType.valueOf(type))
                .categoryId(categoryId)
                .subcategoryId(subcategoryId)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .build();

        Page<Car> cars = carService.searchCars(searchCarsDto, pageable);
        return Response.of(HttpStatus.OK, cars);
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
