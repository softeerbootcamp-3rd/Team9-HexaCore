package com.hexacore.tayo.car;

import com.hexacore.tayo.car.model.ModelDto;
import com.hexacore.tayo.common.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cars")
public class CarController {

    @Autowired
    private CarService carService;

    @PostMapping("/categories")
    public ResponseEntity<ResponseDto> addCategories(@RequestBody ModelDto modelDto) {
        return new ResponseEntity<>(carService.createCategory(modelDto), HttpStatus.OK);
    }

}
