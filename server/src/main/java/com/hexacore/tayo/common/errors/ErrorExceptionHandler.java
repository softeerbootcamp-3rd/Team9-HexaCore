package com.hexacore.tayo.common.errors;

import com.hexacore.tayo.common.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleException(Exception e) {
        return Response.of(ErrorCode.SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleValidationExceptions(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        String message = result.getFieldErrors().get(0).getDefaultMessage();
        return Response.of(ErrorCode.VALIDATION_ERROR, message);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Response> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        String message = e.getParameterType() + " 타입의 " + e.getParameterName() + " 파라미터가 없습니다.";
        return Response.of(ErrorCode.VALIDATION_ERROR, message);
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<Response> handleGeneralException(GeneralException e) {
        return Response.of(e.getErrorCode());
    }
}
