package com.hexacore.tayo.common.errors;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {
    private final ErrorCode errorCode;

    public AuthException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
