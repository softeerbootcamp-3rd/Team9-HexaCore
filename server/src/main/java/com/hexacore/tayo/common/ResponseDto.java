package com.hexacore.tayo.common;

import com.hexacore.tayo.common.errors.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
@RequiredArgsConstructor
public class ResponseDto {

    private final Boolean success;
    private final Integer code;
    private final String message;

    public static ResponseDto success(HttpStatus status) {
        return new ResponseDto(true, status.value(), status.getReasonPhrase());
    }

    public static ResponseDto error(ErrorCode errorCode, Exception e) {
        return new ResponseDto(false, errorCode.getCode(), errorCode.getErrorMessage(e));
    }

    public static ResponseDto error(ErrorCode errorCode, String message) {
        return new ResponseDto(false, errorCode.getCode(), errorCode.getErrorMessage(message));
    }
}
