package com.hexacore.tayo.common.errors;

import com.hexacore.tayo.common.response.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto> handleException(Exception e) {
        return new ResponseEntity<>(ResponseDto.error(ErrorCode.SERVER_ERROR, e),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto> handleValidationExceptions(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        String message = result.getFieldErrors().get(0).getDefaultMessage();
        return new ResponseEntity<>(ResponseDto.error(ErrorCode.VALIDATION_ERROR, message),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ResponseDto> handleGeneralException(GeneralException e) {
        return new ResponseEntity<>(ResponseDto.error(e.getErrorCode(), e),
                e.getErrorCode().getHttpStatus());
    }
}
