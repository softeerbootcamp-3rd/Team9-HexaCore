package com.hexacore.tayo.common.errors;

import com.hexacore.tayo.common.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse> handleException() {
        return CommonResponse.error(ErrorCode.SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        String message = result.getFieldErrors().get(0).getDefaultMessage();
        return CommonResponse.error(ErrorCode.VALIDATION_ERROR, message);
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<CommonResponse> handleGeneralException(GeneralException e) {
        return CommonResponse.error(e.getErrorCode());
    }
}
