package com.hexacore.tayo.test.controller;

import com.hexacore.tayo.common.DataResponseDto;
import com.hexacore.tayo.common.PageResponseDto;
import com.hexacore.tayo.common.ResponseDto;
import com.hexacore.tayo.test.model.TestDto;
import com.hexacore.tayo.test.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping("/data")
    public ResponseEntity<DataResponseDto<TestDto>> getDataTest() {
        return new ResponseEntity<>(testService.testDataResponse(), HttpStatus.OK);
    }

    @GetMapping("/response")
    public ResponseEntity<ResponseDto> getResponseTest() {
        return new ResponseEntity<>(testService.testResponse(), HttpStatus.OK);
    }

    @GetMapping("/exception")
    public ResponseEntity<ResponseDto> getException() {
        return new ResponseEntity<>(testService.testException(), HttpStatus.OK);
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponseDto<TestDto>> getPage() {
        return new ResponseEntity<>(testService.testPage(), HttpStatus.OK);
    }
}
