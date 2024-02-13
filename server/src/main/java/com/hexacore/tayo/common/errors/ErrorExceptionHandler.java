package com.hexacore.tayo.common.errors;

import com.hexacore.tayo.common.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleException() {
        return Response.of(ErrorCode.SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleValidationExceptions(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        String message = result.getFieldErrors().get(0).getDefaultMessage();
        return Response.of(ErrorCode.VALIDATION_ERROR, message);
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<Response> handleGeneralException(GeneralException e) {
        return Response.of(e.getErrorCode());
    }
}
