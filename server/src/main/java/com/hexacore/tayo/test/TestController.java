package com.hexacore.tayo.test;

import com.hexacore.tayo.common.DataResponseDto;
import com.hexacore.tayo.common.PageResponseDto;
import com.hexacore.tayo.common.ResponseDto;
import com.hexacore.tayo.test.model.TestDto;
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
        DataResponseDto<TestDto> response = testService.testDataResponse();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    @GetMapping("/response")
    public ResponseEntity<ResponseDto> getResponseTest() {
        ResponseDto response = testService.testResponse();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    @GetMapping("/exception")
    public ResponseEntity<ResponseDto> getException() {
        ResponseDto response = testService.testException();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponseDto<TestDto>> getPage() {
        PageResponseDto<TestDto> response = testService.testPage();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }
}
