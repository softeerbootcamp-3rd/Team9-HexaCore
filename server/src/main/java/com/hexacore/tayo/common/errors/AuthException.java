package com.hexacore.tayo.common.errors;

import lombok.Getter;

@Getter
public class AuthException extends GeneralException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
