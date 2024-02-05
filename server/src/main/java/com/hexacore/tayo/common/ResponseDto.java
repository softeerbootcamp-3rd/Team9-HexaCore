package com.hexacore.tayo.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ResponseDto {
    private final Boolean success;
    private final Integer code;
    private final String message;

    public static ResponseDto success(ResponseCode code) {
        return new ResponseDto(true, code.getCode(), code.getMessage());
    }

    public static ResponseDto success(ResponseCode code, String message) {
        return new ResponseDto(true, code.getCode(), code.getMessage(message));
    }

    public static ResponseDto error(ResponseCode errorCode, Exception e) {
        return new ResponseDto(false, errorCode.getCode(), errorCode.getMessage(e));
    }

    public static ResponseDto error(ResponseCode errorCode, String message) {
        return new ResponseDto(false, errorCode.getCode(), errorCode.getMessage(message));
    }

}
