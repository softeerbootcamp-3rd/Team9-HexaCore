package com.hexacore.tayo.common.errors;

import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {

    private final ErrorCode errorCode;

    public GeneralException() {
        super(ErrorCode.SERVER_ERROR.getErrorMessage());
        this.errorCode = ErrorCode.SERVER_ERROR;
    }

    public GeneralException(String message) {
        super(ErrorCode.SERVER_ERROR.getErrorMessage(message));
        this.errorCode = ErrorCode.SERVER_ERROR;
    }

    public GeneralException(String message, Throwable cause) {
        super(ErrorCode.SERVER_ERROR.getErrorMessage(message), cause);
        this.errorCode = ErrorCode.SERVER_ERROR;
    }

    public GeneralException(Throwable cause) {
        super(ErrorCode.SERVER_ERROR.getErrorMessage(cause));
        this.errorCode = ErrorCode.SERVER_ERROR;
    }

    public GeneralException(ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }

    public GeneralException(ErrorCode errorCode, String message) {
        super(errorCode.getErrorMessage(message));
        this.errorCode = errorCode;
    }

    public GeneralException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getErrorMessage(cause), cause);
        this.errorCode = errorCode;
    }
}
