package com.hexacore.tayo.test;

import com.hexacore.tayo.car.model.ModelEntity;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping("/data")
    public ResponseEntity<?> getDataTest() {
        return CommonResponse.data(HttpStatus.OK, testService.testDataResponse());
    }

    @GetMapping("/response")
    public ResponseEntity<?> getResponseTest() {
        boolean success = testService.testResponse();
        if (success) {
            return CommonResponse.response(HttpStatus.OK);
        } else {
            return CommonResponse.error(ErrorCode.SERVER_ERROR);
        }
    }

    @GetMapping("/exception")
    public ResponseEntity<?> getException() {
        return CommonResponse.data(HttpStatus.OK, testService.testException());
    }

    @GetMapping("/page")
    public ResponseEntity<?> getPage() {
        Page<ModelEntity> page = testService.testPage();
        return CommonResponse.pagination(HttpStatus.OK, page);
    }
}
