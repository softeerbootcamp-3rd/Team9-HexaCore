package com.hexacore.tayo.common.errors;

import java.util.Optional;
import java.util.function.Predicate;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 엑세스 토큰입니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 엑세스 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 리프레시 토큰입니다."),

    PAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 페이지를 찾을 수 없습니다."),

    EMPTY_USER_ID(HttpStatus.BAD_REQUEST, "유저 아이디를 입력해주세요."),
    EMPTY_USER_PASSWORD(HttpStatus.BAD_REQUEST, "유저 비밀번호를 입력해주세요."),
    EMPTY_USER_NAME(HttpStatus.BAD_REQUEST, "유저 이름을 입력해주세요."),
    EMPTY_USER_EMAIL(HttpStatus.BAD_REQUEST, "유저 이메일을 입력해주세요."),
    USER_ID_DUPLICATED(HttpStatus.BAD_REQUEST, "중복되는 유저 id 입니다."),
    USER_EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST, "중복되는 유저 이메일 입니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "유저가 존재하지 않습니다."),
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요한 서비스 입니다"),
    USER_WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 틀렸습니다."),

    CAR_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 차량입니다."),
    CAR_IMAGE_INSUFFICIENT(HttpStatus.BAD_REQUEST, "이미지를 5개 이상 등록해야 합니다."),
    CAR_MODEL_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 모델명입니다."),
    CAR_NUMBER_DUPLICATED(HttpStatus.BAD_REQUEST, "중복된 차량 번호입니다."),
    USER_ALREADY_HAS_CAR(HttpStatus.BAD_REQUEST, "유저가 이미 차량을 등록했습니다."),
    IMAGE_INDEX_MISMATCH(HttpStatus.BAD_REQUEST, "이미지 개수와 인덱스 개수가 일치하지 않습니다."),

    S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3 업로드에 실패했습니다."),
    INVALID_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 타입입니다."),

    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 문제 발생, 다음에 시도해주세요.");
    public final HttpStatus httpStatus;
    public final String errorMessage;

    ErrorCode(HttpStatus httpStatus, String errorMessage) {
        this.errorMessage = errorMessage;
        this.httpStatus = httpStatus;
    }

    public Integer getCode() {
        return this.httpStatus.value();
    }

    public String getErrorMessage(Throwable e) {
        return this.getErrorMessage(e.getMessage());
    }

    public String getErrorMessage(String message) {
        return Optional.ofNullable(message)
                .filter(Predicate.not(String::isBlank))
                .orElse(this.getErrorMessage());
    }
}
