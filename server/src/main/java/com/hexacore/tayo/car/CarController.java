package com.hexacore.tayo.car;

import com.hexacore.tayo.car.model.CarUpdateDto;
import com.hexacore.tayo.car.model.DateListDto;
import com.hexacore.tayo.car.model.PostCarDto;
import com.hexacore.tayo.common.Response;
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
        return Response.of(HttpStatus.OK, carService.getCategories());
    }

    @PostMapping()
    public ResponseEntity<Response> createCar(@ModelAttribute PostCarDto postCarDto) {
        if (!carService.createCar(postCarDto)) {
            return Response.of(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return Response.of(HttpStatus.CREATED);
    }

    @DeleteMapping("/{carId}")
    public ResponseEntity<Response> deleteCar(@PathVariable Long carId) {
        if (!carService.deleteCar(carId)) {
            return Response.of(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return Response.of(HttpStatus.OK);
    }


    @GetMapping("{carId}")
    public ResponseEntity<Response> carDetail(@PathVariable Long carId) {
        return Response.of(HttpStatus.OK, carService.carDetail(carId));
    }

    @PutMapping("{carId}")
    public ResponseEntity<Response> carUpdate(@PathVariable Long carId, @ModelAttribute CarUpdateDto carUpdateDto) {
        if (!carService.carUpdate(carId, carUpdateDto)) {
            return Response.of(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return Response.of(HttpStatus.ACCEPTED);
    }

    @PutMapping("{carId}/date")
    public ResponseEntity<Response> updateDates(@PathVariable Long carId, @RequestBody DateListDto dateListDto) {
        if (!carService.updateDates(carId, dateListDto)) {
            return Response.of(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return Response.of(HttpStatus.ACCEPTED);
    }
}
