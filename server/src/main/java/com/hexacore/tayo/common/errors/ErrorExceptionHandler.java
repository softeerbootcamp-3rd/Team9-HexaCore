package com.hexacore.tayo.common.errors;

import com.hexacore.tayo.common.ResponseCode;
import com.hexacore.tayo.common.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto> handleException(Exception e) {
        return new ResponseEntity<>(ResponseDto.error(ResponseCode.INTERNAL_SERVER_ERROR, e), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ResponseDto> handleGeneralException(GeneralException e) {
        return new ResponseEntity<>(ResponseDto.error(e.getErrorCode(), e.getMessage()), e.getErrorCode().getHttpStatus());
    }
}
